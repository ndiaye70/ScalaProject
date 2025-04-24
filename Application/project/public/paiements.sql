create table paiements
(
    id            bigserial
        primary key,
    patient_id    bigint         not null,
    montant       numeric(21, 2) not null,
    mode          varchar        not null,
    date_paiement timestamp      not null,
    reference     varchar
);

alter table paiements
    owner to postgres;

