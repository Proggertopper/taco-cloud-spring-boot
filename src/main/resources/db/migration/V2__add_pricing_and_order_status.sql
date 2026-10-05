alter table ingredient add column price decimal(8,2);

update ingredient set price = 1.50 where id = 'FLTO';
update ingredient set price = 1.25 where id = 'COTO';
update ingredient set price = 3.25 where id = 'GRBF';
update ingredient set price = 3.50 where id = 'CARN';
update ingredient set price = 0.80 where id = 'TMTO';
update ingredient set price = 0.70 where id = 'LETC';
update ingredient set price = 1.10 where id = 'CHED';
update ingredient set price = 1.20 where id = 'JACK';
update ingredient set price = 0.65 where id = 'SLSA';
update ingredient set price = 0.75 where id = 'SRCR';

alter table taco_order add column status varchar(32);
update taco_order set status = 'NEW' where status is null;
