/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2016>  eGovernments Foundation
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

jQuery('#btnsearch').click(function(e) {
		
		callAjaxSearch();
	});


$('form').keypress(function (e) {
    if (e.which == 13) {
    	e.preventDefault();
    	callAjaxSearch();
    }
}); 

function getFormData($form){
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};

    $.map(unindexed_array, function(n, i){
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
					url : "/council/councilmeeting/searchmeeting-tocreatemom",      
					type: "POST",
					"data":  getFormData(jQuery('form'))
				},
				"bDestroy" : true,
				"autoWidth": false,
				"sDom" : "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-xs-3'i><'col-xs-3 col-right'l><'col-xs-3 col-right'<'export-data'T>><'col-xs-3 text-right'p>>",
				"aLengthMenu" : [ [ 10, 25, 50, -1 ], [ 10, 25, 50, "All" ] ],
				"oTableTools" : {
					"sSwfPath" : "../../../../../../egi/resources/global/swf/copy_csv_xls_pdf.swf",
					"aButtons" : [{"sExtends" : "xls"},
						           {"sExtends" :"pdf"},
						           {"sExtends" : "print"}]
				},
				aaSorting: [],				
				columns : [ { 
"data" : "committeeType", "sClass" : "text-left"} ,{ 
"data" : "meetingNumber", "sClass" : "text-left"} ,{ 
"data" : "meetingDate", "sClass" : "text-left"},{
"data" : "meetingLocation", "sClass" : "text-left"},{
"data" : "meetingTime", "sClass" : "text-left"}
,{ "data" : null, "target":-1,
	
    sortable: false,
    "render": function ( data, type, full, meta ) {
          	
          	return '<button type="button" class="btn btn-xs btn-secondary view"><span class="glyphicon glyphicon-tasks"></span>&nbsp;Create MOM</button>';
    }
}
,{ "data": "id", "visible":false }
]				
			});
			}



$("#resultTable").on('click','tbody tr td  .view',function(event) {
	var id = reportdatatable.fnGetData($(this).parent().parent(),6);
	window.open('/council/councilmom/new' + '/'+id,'','width=800, height=600,scrollbars=yes');
});



String.prototype.compose = (function (){
	   var re = /\{{(.+?)\}}/g;
	   return function (o){
	       return this.replace(re, function (_, k){
	           return typeof o[k] != 'undefined' ? o[k] : '';
	       });
	   }
}());

var tbody = $('#sumotoTable').children('tbody');
var table = tbody.length ? tbody : $('#sumotoTable');

var row = '<tr>'+
 '<td><select name="meetingMOMs[{{idx}}].preamble.department" class="form-control" required="required"> <option value="">Loading...</option></select></td>'+
 '<td><div class="input-group"><textarea class="form-control textarea-content" required="required" name="meetingMOMs[{{idx}}].preamble.gistOfPreamble"  value="{{gistTextBoxValue}}" /><span class="input-group-addon" id="showModal" data-header="Sumoto Resolution - GIST of Sumoto Resolution"><span class="glyphicon glyphicon-pencil" style="cursor:pointer"></span></span></div></td>'+
 '<td><input type="text" class="form-control" name="meetingMOMs[{{idx}}].preamble.sanctionAmount" {{readonly}} data-pattern="number" value="{{amountTextBoxValue}}"/></td>'+
 '<td><div class="input-group"><textarea class="form-control textarea-content" required="required" name="meetingMOMs[{{idx}}].resolutionDetail"  value="{{amountTextBoxValue}}" /><span class="input-group-addon" id="showModal" data-header="Sumoto Resolution - Resolution comments"><span class="glyphicon glyphicon-pencil" style="cursor:pointer"></span></span></div></td>'+
 '<td><select name="meetingMOMs[{{idx}}].resolutionStatus" class="form-control" required="required"><option value="">Loading...</option></select></td>'+
'</tr>';

jQuery('#add-sumoto').click(function(){
	$('.agenda-section').show();
	
	var idx=$(tbody).data('existing-len')+$(tbody).find('tr').length;
	var sno=idx+1;
	
	console.log('Row Idx', idx);
	
//Add row
	var row={
	       'sno': sno,
	       'idx': idx
	   };
	addRowFromObject(row);
	loadDepartmentlist("meetingMOMs["+idx+"].preamble.department");
	loadResolutionlist("meetingMOMs["+idx+"].resolutionStatus");
	loadWardnumberlist("meetingMOMs["+idx+"].preamble.wardNumber");
});

//ajax call 
function loadDepartmentlist(selectBoxName){

console.log('selectboxName', selectBoxName);

 $.ajax({
		url: "/council/councilmom/departmentlist",     
		type: "GET",
		async: false,
		dataType: "json",
		success: function (response) {
		    console.log("success", response);
			$('select[name="'+selectBoxName+'"]').empty();
			$('select[name="'+selectBoxName+'"]').append($("<option value=''>Select </option>"));
			$.each(response.departmentLists, function(index, departmentLists) {
				$('select[name="'+selectBoxName+'"]').append($('<option>').val(departmentLists.id).text(departmentLists.name));
			});
		}, 
		error: function (response) {
			console.log("failed");
		}
	});
 
 
}

function loadResolutionlist(selectBoxNameResolution){

	 $.ajax({
			url: "/council/councilmom/resolutionlist",     
			type: "GET",
			async: false,
			dataType: "json",
			success: function (response) {
			    console.log("success", response);
				$('select[name="'+selectBoxNameResolution+'"]').empty();
				$('select[name="'+selectBoxNameResolution+'"]').append($("<option value=''>Select </option>"));
				$.each(response.resolutionLists, function(index, resolutionLists) {
					$('select[name="'+selectBoxNameResolution+'"]').append($('<option>').val(resolutionLists.id).text(resolutionLists.code));
				});
			}, 
			error: function (response) {
				console.log("failed");
			}
		});
	 
	 
	}

function loadWardnumberlist(selectBoxNameWard){

	 $.ajax({
			url: "/council/councilmom/wardlist",     
			type: "GET",
			async: false,
			dataType: "json",
			success: function (response) {
			    console.log("success", response);
				$('select[name="'+selectBoxNameWard+'"]').empty();
				$('select[name="'+selectBoxNameWard+'"]').append($("<option value=''>Select </option>"));
				$.each(response.wardLists, function(index, wardLists) {
					$('select[name="'+selectBoxNameWard+'"]').append($('<option>').val(wardLists.id).text(wardLists.name));
				});
			}, 
			error: function (response) {
				console.log("failed");
			}
		});
	 
	 
	}

function addRowFromObject(rowJsonObj)
{
	console.log("Row composed", row.compose(rowJsonObj));
	table.append(row.compose(rowJsonObj));
}



$(document).ready(function() {
	    
    var table_rowindex = 0;
    var tablecolumn_index = 0;
    var tableheaderid ='';
    var tableid='';
    
    $(document).on('blur','.textarea-content', function(evt) {
    	$(this).tooltip('hide')
        .attr('data-original-title', $(this).val());
    	evt.stopImmediatePropagation();
    });
    
    $(document).on('click','#showModal',function(evt){
    	table_rowindex = $(this).closest('tr').index();
    	tablecolumn_index = $(this).closest('td').index();
    	tableheaderid = $(this).data('header');
    	tableid = $(this).closest('table').attr('id');
    	$('#textarea-header').html(tableheaderid);
    	$('#textarea-updatedcontent').val($(this).parent().parent().find('textarea').val());
    	$("#textarea-modal").modal('show');
    	evt.stopImmediatePropagation();
    });
    
    //update textarea content in table wrt index
    $(document).on('click','#textarea-btnupdate',function(evt){
    	$('#'+tableid +' tbody tr:eq(' + table_rowindex + ') td:eq('+tablecolumn_index+')').find('textarea').val($('#textarea-updatedcontent').val()).tooltip('hide')
        .attr('data-original-title', $('#textarea-updatedcontent').val());
    	evt.stopImmediatePropagation();
    });

});

$('#buttonFinalSubmit')
.click(
		function(e) {
					if ($('form').valid()) {
							bootbox
							.confirm({
								message : 'Information entered in this screen will not be modified once submitted,Please confirm yes to save',
								buttons : {
									'cancel' : {
										label : 'No',
										className : 'btn-danger pull-right'
									},
									'confirm' : {
										label : 'Yes',
										className : 'btn-danger pull-right'
									}
								},
								callback : function(result) {
									if (result) {
										 var action = '/council/councilmom/generateresolution';
								 			$('#councilMomform').attr('method', 'get');
								 			$('#councilMomform').attr('action', action); 
								 			document.forms["councilMomform"].submit();
									} else {
										e.stopPropagation();
										e.preventDefault();
									}
								}
							});
			} else {
				e.stopPropagation();
				e.preventDefault();
			}
});
