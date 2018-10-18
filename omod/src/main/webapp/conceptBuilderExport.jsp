<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp" %>

<div id="page">
	<div id="container">
        <table>
            <tr>
                <td style="width:40%; text-align:left; vertical-align: top;">
                    <h1>Enter Concepts to Export</h1>
                    <form method="post">
                        <textarea name="concepts" rows="30" cols="50">${concepts}</textarea>
                        <br/>
                        <input type="submit" value="Submit"/>
                    </form>
                </td>
                <td style="width:60%; text-align:left; vertical-align: top;">
                    <c:if test="${!empty concepts}">
                        <h3>Results:</h3>
                        Concepts entered: ${fn:length(conceptIdsOrUuids)}
                        <br/>
                        Concepts included: ${fn:length(alreadyDone)}
                        <br/>
                        <h3>Warnings/Errors:</h3>
                        <ul>
                            <c:forEach items="${errorMessages}" var="errorMessage"><li>${errorMessage}</li></c:forEach>
                        </ul>
                    </c:if>
                </td>
            </tr>
        </table>
        <div>
            <c:if test="${!empty concepts}">
                <hr/>
                <h3>Source Code:</h3>
                <pre><c:forEach items="${linesOfCode}" var="lineOfCode">${lineOfCode}</c:forEach></pre>
            </c:if>
        </div>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
