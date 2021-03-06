ALTER TABLE EGW_ESTIMATE_APPROPRIATION ADD COLUMN version bigint DEFAULT 0;

ALTER TABLE EGW_ESTIMATE_APPROPRIATION RENAME COLUMN ABSTRACTESTIMATE_ID TO ABSTRACTESTIMATE;
ALTER TABLE EGW_ESTIMATE_APPROPRIATION RENAME COLUMN budgetusage_id TO budgetusage;
ALTER TABLE EGW_ESTIMATE_APPROPRIATION RENAME COLUMN depositworksusage_id TO depositworksusage;

ALTER TABLE EGW_ESTIMATE_APPROPRIATION ADD COLUMN CREATEDBY bigint NOT NULL;
ALTER TABLE EGW_ESTIMATE_APPROPRIATION ADD COLUMN CREATEDDATE timestamp without time zone NOT NULL;
ALTER TABLE EGW_ESTIMATE_APPROPRIATION ADD COLUMN lastmodifiedby bigint;
ALTER TABLE EGW_ESTIMATE_APPROPRIATION ADD COLUMN lastmodifieddate timestamp without time zone;

ALTER TABLE EGW_ESTIMATE_APPROPRIATION ADD CONSTRAINT fk_estimateappropriation_createdby FOREIGN KEY (createdby) REFERENCES eg_user (id);
ALTER TABLE EGW_ESTIMATE_APPROPRIATION ADD CONSTRAINT fk_estimateappropriation_modifiedby FOREIGN KEY (lastmodifiedby) REFERENCES eg_user (id); 
  
--rollback ALTER TABLE EGW_ESTIMATE_APPROPRIATION DROP COLUMN lastmodifieddate;
--rollback ALTER TABLE EGW_ESTIMATE_APPROPRIATION DROP COLUMN lastmodifiedby;
--rollback ALTER TABLE EGW_ESTIMATE_APPROPRIATION DROP COLUMN CREATEDDATE;
--rollback ALTER TABLE EGW_ESTIMATE_APPROPRIATION DROP COLUMN CREATEDBY;

--rollback ALTER TABLE EGW_ESTIMATE_APPROPRIATION RENAME COLUMN depositworksusage TO depositworksusage_id;
--rollback ALTER TABLE EGW_ESTIMATE_APPROPRIATION RENAME COLUMN budgetusage TO budgetusage_id;
--rollback ALTER TABLE EGW_ESTIMATE_APPROPRIATION RENAME COLUMN ABSTRACTESTIMATE TO ABSTRACTESTIMATE_ID;
--rollback ALTER TABLE EGW_ESTIMATE_APPROPRIATION DROP COLUMN version;