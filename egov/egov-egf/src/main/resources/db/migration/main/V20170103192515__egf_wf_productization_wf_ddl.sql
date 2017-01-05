 alter table egf_budget add column processid varchar(32);
 alter table egf_budgetdetail  add column processid varchar(32);
 alter table paymentheader  add column processid varchar(32);
 alter table voucherheader  add column processid varchar(32);
 alter table eg_billregister  add column processid varchar(32);

update eg_wf_types set enabled =false where type='CVoucherHeader';
update eg_wf_types set enabled =false where type='EgBillregister';
update eg_wf_types set enabled =false where type='Paymentheader';
update eg_wf_types set enabled =false where type='Budget';
update eg_wf_types set enabled =false where type='BudgetDetail';
