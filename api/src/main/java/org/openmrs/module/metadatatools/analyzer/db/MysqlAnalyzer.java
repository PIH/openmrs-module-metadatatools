package org.openmrs.module.metadatatools.analyzer.db;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MysqlAnalyzer {

    @Autowired
    DbSessionFactory dbSessionFactory;

    public List<ForeignKeyReference> getForeignKeyReferencesToTable(String tableName) {
        List<ForeignKeyReference> ret = new ArrayList<>();
        StringBuilder q = new StringBuilder();
        q.append("select table_name, column_name, referenced_table_name, referenced_column_name ");
        q.append("from information_schema.key_column_usage ");
        q.append("where referenced_table_schema = '").append(OpenmrsConstants.DATABASE_NAME).append("' ");
        q.append("and referenced_table_name = '").append(tableName).append("' ");
        q.append("and referenced_column_name is not null");
        for (Object row : dbSessionFactory.getCurrentSession().createSQLQuery(q.toString()).list()) {
            Object[] cols = (Object[]) row;
            TableColumn column = new TableColumn((String) cols[0], (String) cols[1]);
            TableColumn referencedColumn = new TableColumn((String) cols[2], (String) cols[3]);
            ret.add(new ForeignKeyReference(column, referencedColumn));
        }
        return ret;
    }

    public List<TableColumn> getTextColumnsToCheckForReferences() {
        List<TableColumn> ret = new ArrayList<>();
        StringBuilder q = new StringBuilder();
        q.append("select table_name, column_name ");
        q.append("from information_schema.columns ");
        q.append("where table_schema = '").append(OpenmrsConstants.DATABASE_NAME).append("' ");
        q.append("and (data_type like '%text%' or (data_type = 'varchar' and column_name like '%_uuid%')) ");
        q.append("order by table_name, column_name ");
        for (Object row : dbSessionFactory.getCurrentSession().createSQLQuery(q.toString()).list()) {
            Object[] cols = (Object[]) row;
            ret.add(new TableColumn((String) cols[0], (String) cols[1]));
        }
        return ret;
    }

    public long getNumberOfReferences(ForeignKeyReference foreignKeyReference, Object value) {
        StringBuilder q = new StringBuilder();
        q.append("select count(*) ");
        q.append("from ").append(foreignKeyReference.getForeignKey().getTable()).append(" ");
        q.append("where ").append(foreignKeyReference.getForeignKey().getColumn()).append(" = :value ");
        Number dbResult = (Number) dbSessionFactory.getCurrentSession().createSQLQuery(q.toString()).setParameter("value", value).list().get(0);
        return dbResult.longValue();
    }

    public long getNumberOfTextReferences(TableColumn tableColumn, String reference) {
        StringBuilder q = new StringBuilder();
        q.append("select count(*) ");
        q.append("from ").append(tableColumn.getTable()).append(" ");
        q.append("where ").append(tableColumn.getColumn()).append(" like :value ");
        String value = "%"  + reference + "%";
        Number dbResult = (Number) dbSessionFactory.getCurrentSession().createSQLQuery(q.toString()).setParameter("value", value).list().get(0);
        return dbResult.longValue();
    }
}
