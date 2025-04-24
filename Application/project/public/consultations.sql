create table consultations
(
    id               bigserial
        primary key,
    patient_id       bigint        not null
        constraint consult_patient_fk
            references patients,
    personnel_id     bigint        not null
        constraint consult_personnel_fk
            references personnels,
    date_realisation timestamp     not null,
    symptomes        varchar(1000) not null,
    diagnostic       varchar(2000) not null,
    prescriptions    varchar       not null,
    notes_internes   varchar       not null
);

alter table consultations
    owner to postgres;

