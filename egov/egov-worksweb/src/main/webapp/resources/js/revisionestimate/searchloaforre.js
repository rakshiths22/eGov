/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

$('#btncreatere').hide();

jQuery('#btnsearch').click(function(e) {
	var fromDate = '';
	var toDate = '';
	if($('#fromDate').val() != "") {
		fromDate = $('#fromDate').data('datepicker').date;
	}
	if($('#toDate').val() != "") {
		toDate = $('#toDate').data('datepicker').date;
	}
	var flag = true; 
	if(toDate != '' && fromDate != '') {
		if(fromDate > toDate) {
			flag = false;
			bootbox.alert('To Date should be greater than From Date');
		}
	}
	if(flag)
	callAjaxSearch();
});

function getFormData($form) {
	var unindexed_array = $form.serializeArray();
	var indexed_array = {};

	$.map(unindexed_array, function(n, i) {
		indexed_array[n['name']] = n['value'];
	});

	return indexed_array;
}


function callAjaxSearch() {
	drillDowntableContainer = jQuery("#resultTable");
	jQuery('.report-section').removeClass('display-hide');
	reportdatatable = drillDowntableContainer
			.dataTable({
				ajax : {
					url : "/egworks/letterofacceptance/ajaxsearch-loatocreatere",
					type : "POST",
					"data" : getFormData(jQuery('form'))
				},
				"sPaginationType" : "bootstrap",
				"bDestroy" : true,
				'bAutoWidth': false,
				"sDom" : "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-xs-3'i><'col-xs-3 col-right'l><'col-xs-3 col-right'<'export-data'T>><'col-xs-3 text-right'p>>",
				"aLengthMenu" : [ [ 10, 25, 50, -1 ], [ 10, 25, 50, "All" ] ],
				"oTableTools" : {
					"sSwfPath" : "../../../../../../egi/resources/global/swf/copy_csv_xls_pdf.swf",
					"aButtons" : []
				},
				"fnRowCallback" : function(row, data, index) {
					$('td:eq(0)',row).html('<input type="radio" name="selectCheckbox" value="'+ data.workOrderEstimateId +'"/>');
					if(data.estimateNumber != null)
						$('td:eq(1)',row).html('<a href="javascript:void(0);" onclick="viewAbstractEstimate(\''+ data.estimateId +'\')">' + data.estimateNumber + '</a>');
					if(data.workOrderNumber != null)
						$('td:eq(2)',row).html('<a href="javascript:void(0);" onclick="viewWorkOrder(\''+ data.workOrderId +'\')">' + data.workOrderNumber + '</a>');
					$('td:eq(5)',row).html(parseFloat(Math.round(data.agreementValue * 100) / 100).toFixed(2));
					$('#btncreatere').show();
				},
				aaSorting : [],
				columns : [ { 
					    "data" : ""} ,{ 
						"data" : "estimateNumber"} , {
						"data" : "workOrderNumber"} ,{
						"data" : "workOrderDate"} ,{
						"data" : "contractorName"},{
						"data" : "", "sClass" : "text-right", "sType" : "decimal"} ]
			});
}

function viewWorkOrder(workOrderid) {
	window.open("/egworks/letterofacceptance/view/" + workOrderid, '', 'height=650,width=980,scrollbars=yes,left=0,top=0,status=yes');
}

function viewAbstractEstimate(estimateId) {
	window.open("/egworks/abstractestimate/view/" + estimateId, '', 'height=650,width=980,scrollbars=yes,left=0,top=0,status=yes');
}

$(document).ready(function() {
	var workOrderNumber = new Bloodhound({
		datumTokenizer : function(datum) {
			return Bloodhound.tokenizers.whitespace(datum.value);
		},
		queryTokenizer : Bloodhound.tokenizers.whitespace,
		remote : {
			url : '/egworks/letterofacceptance/ajaxworkordernumbers-createre?workOrderNo=%QUERY',
			filter : function(data) {
				return $.map(data, function(ct) {
					return {
						name : ct
					};
				});
			}
		}
	});

	workOrderNumber.initialize();
	var workOrderNumber_typeahead = $('#workOrderNumber').typeahead({
		hint : true,
		highlight : true,
		minLength : 3
	}, {
		displayKey : 'name',
		source : workOrderNumber.ttAdapter()
	});
	
	var contractorSearch = new Bloodhound({
		datumTokenizer : function(datum) {
			return Bloodhound.tokenizers
					.whitespace(datum.value);
		},
		queryTokenizer : Bloodhound.tokenizers.whitespace,
		remote : {
			url : '/egworks/letterofacceptance/ajaxcontractors-createre?code=%QUERY',
			filter : function(data) {
				return $.map(data, function(ct) {
					return {
						name : ct,
					};
				});
			}
		}
	});

	contractorSearch.initialize();
	var contractorSearch_typeahead = $('#contractor')
		.typeahead({
			hint : true,
			highlight : true,
			minLength : 3
		}, {
			displayKey : 'name',
			source : contractorSearch.ttAdapter()
		});
});

$('#btncreatere').click(function(e) {
	var workOrderEstimateId = $('input[name=selectCheckbox]:checked').val();
	if(workOrderEstimateId == null) {
		bootbox.alert("Please select a Letter of Acceptance to create Revision Estimate");
	}
	else {
		window.location = "/egworks/revisionestimate/create?workOrderEstimateId="+workOrderEstimateId;
	}
});