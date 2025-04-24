create table rendez_vous
(
    id           bigserial
        primary key,
    patient_id   bigint    not null
        constraint patient_fk
            references patients,
    personnel_id bigint    not null
        constraint personnel_fk
            references personnels,
    date_heure   timestamp not null,
    motif        varchar   not null,
    statut       varchar   not null
);

alter table rendez_vous
    owner to postgres;

