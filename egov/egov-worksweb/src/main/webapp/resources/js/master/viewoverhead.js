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
jQuery('#btnsearch').click(function(e) {
		callAjaxSearch();
});
$(document).ready(function(){
    var overheadName = new Bloodhound({
        datumTokenizer: function (datum) {
            return Bloodhound.tokenizers.whitespace(datum.value);
        },
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: {
            url: '/egworks/masters/ajax-searchoverheadname?name=%QUERY',
            filter: function (data) {
                return $.map(data, function (ct) {
                    return {
                        name: ct
                    };
                });
            }
        }
    });
   
    overheadName.initialize();
	var overheadName = $('#overheadName').typeahead({
		hint : true,
		highlight : true,
		minLength : 3
	}, {
		displayKey : 'name',
		source : overheadName.ttAdapter()
	});
});

function callAjaxSearch() {
	drillDowntableContainer = jQuery("#resultTable");
	jQuery('.report-section').removeClass('display-hide');
	reportdatatable = drillDowntableContainer
			.dataTable({
				ajax : {
					url : "/egworks/masters/ajaxsearch-viewoverhead",
					type : "POST",
					"data" : getFormData(jQuery('form'))
				},
				"sPaginationType" : "bootstrap",
				"bDestroy" : true,
				'bAutoWidth' : false,
				"sDom" : "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-xs-3'i><'col-xs-3 col-right'l><'col-xs-3 col-right'<'export-data'T>><'col-xs-3 text-right'p>>",
				"aLengthMenu" : [ [ 10, 25, 50, -1 ], [ 10, 25, 50, "All" ] ],
				"oTableTools" : {
					"sSwfPath" : "../../../../../../egi/resources/global/swf/copy_csv_xls_pdf.swf",
					"aButtons" : []
				},
				"fnRowCallback" : function(row, data, index) {
					$('td:eq(0)', row).html(index + 1);
					$('td:eq(1)', row).html(
							'<a href="javascript:void(0);" onclick="openViewOverhead(\''
									+ data.id + '\')">'
									+ data.name + '</a>');
					return row;
				},
				aaSorting : [],
				columns : [ {
					"data" : "",
					"sClass" : "text-center","sWidth": "1%"
				}, {
					"data" : "name",
					"sClass" : "text-left","sWidth": "20%"
				}, {
					"data" : "description",
					"sClass" : "text-left"
				}, {
					"data" : "startDate",
					"sClass" : "text-left","sWidth": "10%",
					render: function (data, type, full) {
						if(data){
							return getFormattedDate(data);
						}
						return "";
			    	}
				}, {
					"data" : "endDate",
					"sClass" : "text-left","sWidth": "10%",
					render: function (data, type, full) {
						if(data){
							return getFormattedDate(data);
						}
						return "";
			    	}
				} ]
			});
}


function getFormattedDate(dateStr)
{
	var dateObj=new Date(dateStr);
	return addPrefixZero(dateObj.getDate())+"/"+ addPrefixZero(dateObj.getMonth())  +"/"+ dateObj.getFullYear();
}

function addPrefixZero(str)
{
	str=str.toString();	
	return (str.length===1?"0"+str:str);
}

function getFormData($form){
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};

    $.map(unindexed_array, function(n, i){
        indexed_array[n['name']] = n['value'];
    });

    return indexed_array;
}

function openViewOverhead(id) {
	window.open("/egworks/masters/overhead-view/" + id, '', 'height=650,width=980,scrollbars=yes,left=0,top=0,status=yes');
}