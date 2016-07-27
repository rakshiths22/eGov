
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%--
  ~ eGov suite of products aim to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) <2015>  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  --%>


 
<c:choose>
<c:when test="${activity.measurementSheetList.size() > 0 }">
<c:set var="net" value="0" />      
 <td hidden="true">
                             <input class="classmspresent" type="hidden" disabled="disabled" name="sorActivities[${item.index }].mspresent" value="1" id="sorActivities[${item.index }].mspresent" data-idx="0"/>
                             <input class="classmsopen" type="hidden" disabled="disabled" name="sorActivities[${item.index }].msopen" value="0" id="sorActivities[${item.index }].msopen" data-idx="0"/>
                       
                             <span name="sorActivities[${item.index }].mstd" class="sorActivities[${item.index }].mstd" id="sorActivities[${item.index }].mstd" data-idx="0">
    <!--only for validity head start -->                         
    <table>
    <tr>
        <td colspan="9"><!--only for validity head end -->
            <div class="view-content">Measurement Sheet <div class="pull-right"><span class="glyphicon glyphicon-remove-circle error-msg hide-ms" style="cursor:pointer;font-size:16px;"></span></div>
            </div>

            <table class=" table table-bordered" id="sorActivities[${item.index }].mstable">
                <thead>
                <th><spring:message code="lbl.slno" /></th>
                <th><spring:message code="lbl.identifier" /></th>
                <th><spring:message code="lbl.description" /><span class="mandatory"></th>
                <th><spring:message code="lbl.no" /></th>
                <th><spring:message code="lbl.length" /></th>
                <th><spring:message code="lbl.width" /></th>
                <th><spring:message code="lbl.depthorheight" /></th>
                <th><spring:message code="lbl.quantity" /><span class="mandatory"></span></th>
                <th><spring:message code="lbl.delete" /></th>
                </thead>
                <tbody id="msrow1">
                <c:forEach items="${activity.measurementSheetList}" var="ms" varStatus="msindex" >
                <tr id="msrow">
                    <td>
                        <input  name="sorActivities[${item.index }].measurementSheetList[${msindex.index}].slNo" value="${ms.slNo}" readonly="readonly" id="sorActivities[${item.index }].measurementSheetList[${msindex.index}].slNo" class="form-control text-right patternvalidation runtime-update spanslno" data-pattern="decimalvalue" />
                    <input type="hidden"  name="sorActivities[${item.index }].measurementSheetList[${msindex.index}].id" value="${ms.id}"/>
                    <input type="hidden"  id="sample" value="${ms.activity.quantity}"/>
                    <input type="hidden"  name="sorActivities[${item.index }].measurementSheetList[${msindex.index}].activity" value="${ms.activity.id}"/> 
                    </td>
                    <td>
                        <select name="sorActivities[${item.index }].measurementSheetList[${msindex.index}].identifier"    id="sorActivities[${item.index }].measurementSheetList[${msindex.index}].identifier"  onchange="findNet(this)" class="form-control runtime-update"   >
                            <option value="A" <c:if test="${ms.identifier=='A'}"> selected="selected" </c:if>    >+</option>
                            <option value="D" <c:if test="${ms.identifier=='D'}"> selected="selected" </c:if>    >-</option>
                            </select>
                    </td>
                    <td>
                        <textarea name="sorActivities[${item.index }].measurementSheetList[${msindex.index}].remarks"    id="sorActivities[${item.index }].measurementSheetList[${msindex.index}].remarks" class="form-control text-left patternvalidation runtime-update"
                               data-pattern="alphanumeric" maxlength="1024" >${ms.remarks}</textarea>

                    </td>
                    <td>
                        <input name="sorActivities[${item.index }].measurementSheetList[${msindex.index}].no" value="${ms.no}"  maxlength="4" id="sorActivities[${item.index }].measurementSheetList[${msindex.index}].no" class="form-control text-right patternvalidation runtime-update"
                               data-pattern="decimalvalue" data-idx="0" />

                    </td>
                    <td>                                                                     
                        <input name="sorActivities[${item.index }].measurementSheetList[${msindex.index}].length"   maxlength="15" onkeyup="limitCharatersBy10_4(this);" value="${ms.length}"  id="sorActivities[${item.index }].measurementSheetList[${msindex.index}].length" class="form-control text-right patternvalidation runtime-update"
                               data-pattern="decimalvalue" data-idx="0" />

                    </td>
                    <td>
                        <input name="sorActivities[${item.index }].measurementSheetList[${msindex.index}].width"  maxlength="15" onkeyup="limitCharatersBy10_4(this);"   value="${ms.width}"  id="sorActivities[${item.index }].measurementSheetList[${msindex.index}].width" class="form-control text-right patternvalidation runtime-update"
                               data-pattern="decimalvalue"  data-idx="0" />

                    </td><td>
                    <input name="sorActivities[${item.index }].measurementSheetList[${msindex.index}].depthOrHeight"  maxlength="15" onkeyup="limitCharatersBy10_4(this);"  id="sorActivities[${item.index }].measurementSheetList[${msindex.index}].depthOrHeight" class="form-control text-right patternvalidation runtime-update"
                           data-pattern="decimalvalue"  value="${ms.depthOrHeight}" data-idx="0" />

                </td><td>
                    <input name="sorActivities[${item.index }].measurementSheetList[${msindex.index}].quantity" id="sorActivities[${item.index }].measurementSheetList[${msindex.index}].quantity" class="form-control text-right patternvalidation runtime-update"
                           data-pattern="decimalvalue"  value="${ms.quantity}" required="required" onblur="findNet(this)" />
                   
			<c:if test="${ms.identifier=='A'}">
				<c:set var="net" value="${net + ms.quantity}" />
			</c:if> 
			<c:if test="${ms.identifier=='D'}">
				<c:set var="net" value="${net - ms.quantity}" />
			</c:if>
											</td>
                    <td><span class="glyphicon glyphicon-trash" onclick="deleteThisRow(this)" data-idx="${msindex.index}"></span></td>
                </tr>
                </c:forEach>
                <tr>
                    <td colspan="6" class="text-right">
                        <input type="button" value ="Add Row" class="btn btn-xs btn-info add-msrow">
                        <button   class="btn btn-xs btn-danger reset-ms">Reset</button>
                        <input type="button" value="Submit"  id="sorActivities[${item.index }].mssubmit" class="btn btn-xs btn-primary ms-submit"/> 
                    </td>
                    <td class="text-right">Grand Total</td>
                    <td id="sorActivities[${item.index }].msnet"  class="text-right">${net}</td>
                    <td></td>
                </tr>
                
                <tbody>
            </table>
       <!--only for validity tail start -->  
        </td>
        </tr><!--only for validity -->
    </table> <!--only for validity tail end -->  
 
</span>
</td>
</c:when>
<c:otherwise>
<td hidden="true">
<input class="classmspresent" type="hidden" disabled="disabled" name="sorActivities[${item.index }].mspresent" value="0" id="sorActivities[${item.index }].mspresent" data-idx="0"/>
<input class="classmsopen" type="hidden" disabled="disabled" name="sorActivities[${item.index }].msopen" value="0" id="sorActivities[${item.index }].msopen" data-idx="0"/>
<span  class="sorActivities[${item.index }].mstd" id="sorActivities[${item.index }].mstd" data-idx="${item.index }"></span>
</td>
</c:otherwise>
</c:choose>