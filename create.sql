create table address (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, address_id varchar(255), address_type varchar(255), city varchar(255), district varchar(255), latitude double, line1 varchar(255), line2 varchar(255), link varchar(255), longitude double, pincode varchar(6), state varchar(255), primary key (id))
create table auth_session (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, block_time timestamp, is_blocked boolean, is_locked boolean, last_api_access_time timestamp, lock_time timestamp, refresh_token varchar(255), session_ref_id varchar(255), unblock_time timestamp, unlock_time timestamp, user_attempts integer, authentication_id bigint, primary key (id))
create table authentication (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, email varchar(255), last_login timestamp, mobile varchar(14), mpin varchar(255), otp_verification boolean, password varchar(255), username varchar(255), user_type varchar(255), primary key (id))
create table authentication_role (authentication_id bigint not null, role_id bigint not null)
create table bus_fare (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, fare double, fare_id varchar(255), route_id bigint, from_station_id bigint, to_station_id bigint, primary key (id))
create table bus_route (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, association varchar(255), bus_type varchar(255), route_code varchar(255), route_id varchar(255), route_name varchar(255), route_name_down varchar(255), route_name_up varchar(255), vehicle_number varchar(255), primary key (id))
create table bus_station (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, display_name varchar(255), latitude double, longitude double, station_code varchar(255), station_id varchar(255), primary key (id))
create table bus_station_route (station_id bigint not null, route_id bigint not null, primary key (station_id, route_id))
create table bus_timetable (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, arival_time time, departure_time time, route_type varchar(255), sr_num integer, timetable_id varchar(255), trip_number integer, route_id bigint, station_id bigint, primary key (id))
create table card_details (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, bar_code_no varchar(255), card_no varchar(255), card_token varchar(255), authentication_id bigint not null, primary key (id))
create table device_info (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, app_version varchar(255), device_id varchar(255), fcm_token varchar(255), os_type varchar(255), os_version varchar(255), authentication_id bigint, primary key (id))
create table explore (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, banner_link varchar(255), category varchar(255), current_status varchar(255), description clob, disclaimer clob, explore_id varchar(255), explore_type varchar(255), logo_link varchar(255), misc clob, name varchar(255), sub_type varchar(255), target_segment varchar(255), terms_and_conditions clob, ticket_link varchar(255), title varchar(255), website_link varchar(255), address_id bigint, authentication_id bigint, primary key (id))
create table favourite_address (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, address varchar(255), address_id varchar(255), address_title varchar(255), favourite_type varchar(255), latitute double, longitude double, authentication_id bigint, primary key (id))
create table feedback (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, description varchar(1000), authentication_id bigint, category_id bigint, primary key (id))
create table feedback_category (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, category_id varchar(255), description varchar(255), display_name varchar(255), primary key (id))
create table global_config (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, key varchar(255), value clob, primary key (id))
create table journey_mode_details (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, destination varchar(255), destination_id varchar(255), destination_latitude double, destination_longitude double, distance double, estimated_arrival_time time, fare double, intermediate_stops varchar(255), is_booking_allowed boolean, is_ticket_booked boolean, no_of_intermediate_stops integer, route varchar(255), source varchar(255), source_id varchar(255), source_latitude double, source_longitude double, ticket_id varchar(255), time time, travel_time double, type varchar(255), journey_planner_route_id bigint, primary key (id))
create table journey_planner_route (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, amount double, arrival_time time, departure_time time, journey_planner_id varchar(255), total_distance double, total_duration bigint, authentication_id bigint, primary key (id))
create table metro_line (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, display_name varchar(255), line_code varchar(255), line_id varchar(255), primary key (id))
create table metro_station (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, display_name varchar(255), distance double, geo_id varchar(255), latitude double, longitude double, sel_station_id integer, station_code varchar(255), station_code_dn varchar(255), station_code_up varchar(255), station_id varchar(255), primary key (id))
create table metro_timetable (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, arival_time time, departure_time time, dwelltime bigint, metro_line_name varchar(255), prev_runtime bigint, sr_num bigint, station_name varchar(255), timetable_id varchar(255), total_runtime bigint, metro_station_id bigint, metro_trip_id bigint, primary key (id))
create table metro_trip (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, mt_trip_id varchar(255), direction varchar(255), next_number varchar(255), prev_number varchar(255), service_id varchar(255), start_time time, total_distance varchar(255), trip_id varchar(255), trip_number varchar(255), primary key (id))
create table mpin_log (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, mpin varchar(255), authentication_id bigint, primary key (id))
create table notification (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, action varchar(255), banner_url varchar(255), notification_body varchar(1000), creation_date_time timestamp, notification_ref_id varchar(255), seen boolean, status varchar(255), sub_title varchar(255), notification_title varchar(255), type varchar(255), type_id varchar(255), authentication_id bigint, primary key (id))
create table occupation (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, display_name varchar(255), occupation_id varchar(255), primary key (id))
create table roles (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, description varchar(255), name varchar(255), primary key (id))
create table slot (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, end_date date, end_time varchar(255), fees varchar(255), start_date date, start_time varchar(255), ticket_type varchar(255), explore_id bigint, primary key (id))
create table station_metro_line (metro_station_id bigint not null, metro_line_id bigint not null, primary key (metro_station_id, metro_line_id))
create table ticket (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, description varchar(255), journey_date varchar(255), ticket_fare double, ticket_gu_id varchar(255), ticket_no varchar(255), ticket_ref_id varchar(255), ticket_status varchar(255), ticket_transaction_id varchar(255), ticket_type varchar(255), transport_mode varchar(255), travellers smallint, authentication_id bigint, from_station_id bigint, to_station_id bigint, transaction_id bigint not null, primary key (id))
create table transaction (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, amount double, final_txn_status varchar(255), order_id varchar(255), payment_service_provider varchar(255), psp_payment_method varchar(255), psp_payment_method_type varchar(255), psp_ref_id varchar(255), psp_status varchar(255), psp_txn_id varchar(255), service_provider varchar(255), sp_ref_id varchar(255), sp_status varchar(255), sp_txn_id varchar(255), txn_completed_on timestamp, txn_initiated_on timestamp not null, txn_type varchar(255), authentication_id bigint, linked_txn bigint, primary key (id))
create table user_detail (id bigint generated by default as identity, created_at timestamp not null, is_active boolean, updated_at timestamp, dob date, first_name varchar(255), gender varchar(255), last_name varchar(255), middle_name varchar(255), occupation varchar(255), pg_customer_id varchar(255), user_configuration clob, user_id varchar(255), authentication_id bigint not null, primary key (id))
alter table authentication add constraint UK_hybpl4viqj41ymymrgcvngdq8 unique (mobile)
alter table authentication add constraint UK_1onqo0ddylb38iwxpjw31w8ly unique (username)
alter table bus_route add constraint UK_4re47dco1edeyoxwwuq78x0f unique (route_code)
alter table bus_station add constraint UK_3xq0fit0w45swnkykqkapxu0x unique (station_code)
alter table card_details add constraint UK_t4pr9o4gh3wutgbc4y1f6akpi unique (authentication_id)
alter table global_config add constraint UK_5q6qsmx5yskghvo7ylv1o1shi unique (key)
alter table ticket add constraint UK_8ncd0v6pcvxp48r25wqnlg10w unique (transaction_id)
alter table user_detail add constraint UK_pwui8ys715k3hyoxkcim4ut78 unique (authentication_id)
alter table auth_session add constraint FK86b3wlvr4ubcrfccnkk7li9mj foreign key (authentication_id) references authentication
alter table authentication_role add constraint FK5p0joto3xy92gf0umpgmxypjf foreign key (role_id) references roles
alter table authentication_role add constraint FKaqwplgjfenp55ogucy8pyscbg foreign key (authentication_id) references authentication
alter table bus_fare add constraint FK4a1tx6m4m9blubxe4fn7ynq17 foreign key (route_id) references bus_route
alter table bus_fare add constraint FK8hoy85is8yeptmvt9tw7u567p foreign key (from_station_id) references bus_station
alter table bus_fare add constraint FKqoouv4djnttnvu85idyun8id6 foreign key (to_station_id) references bus_station
alter table bus_station_route add constraint FKrbqtf0j0dhj5y89fg0glwkmu1 foreign key (route_id) references bus_route
alter table bus_station_route add constraint FKh551pa0xdljiv4u2b0s72jakb foreign key (station_id) references bus_station
alter table bus_timetable add constraint FKss3luopxwupjptd88yhgqf1fj foreign key (route_id) references bus_route
alter table bus_timetable add constraint FKhurbhwlbtewfa74xebfc9sn1y foreign key (station_id) references bus_station
alter table card_details add constraint FK91gsa4mvy6jiwvlrk44x31j1c foreign key (authentication_id) references authentication
alter table device_info add constraint FKip5tixrg91tff7121ofcuqfa6 foreign key (authentication_id) references authentication
alter table explore add constraint FKrpupovufcsl8ejcgn7o0xsfm9 foreign key (address_id) references address
alter table explore add constraint FK7lnlcibwbfi3mgh8tf0asjcut foreign key (authentication_id) references authentication
alter table favourite_address add constraint FKn36iyv2w1fcks1g5fn6mwi31c foreign key (authentication_id) references authentication
alter table feedback add constraint FKsw4xvry917djk0buy5hlcagpw foreign key (authentication_id) references authentication
alter table feedback add constraint FKicyj3i5f99929xjd8e3h0n5pb foreign key (category_id) references feedback_category
alter table journey_mode_details add constraint FKicvr9f7a0iljmb975evt73hnk foreign key (journey_planner_route_id) references journey_planner_route
alter table journey_planner_route add constraint FK1k2h30tyhhono4cqgtnarxanm foreign key (authentication_id) references authentication
alter table metro_timetable add constraint FKpsu4xouwm6vd1k2a8w9qwg4mn foreign key (metro_station_id) references metro_station
alter table metro_timetable add constraint FK7n565lhhwqnb9wj38rmhckg1s foreign key (metro_trip_id) references metro_trip
alter table mpin_log add constraint FKfpofdqj03l87k6l8cwidq0hut foreign key (authentication_id) references authentication
alter table notification add constraint FKdugrrq7mhxy9n1u2p6qvchbcc foreign key (authentication_id) references authentication
alter table slot add constraint FKmyj9cn9lvf4a37o6k9161q33u foreign key (explore_id) references explore
alter table station_metro_line add constraint FKiwegala764fkwhmde0oshj5c foreign key (metro_line_id) references metro_line
alter table station_metro_line add constraint FKjm6bw1aay17r7b8s26obdu4e5 foreign key (metro_station_id) references metro_station
alter table ticket add constraint FKkk249hr2dht2x77pnvbfsto2u foreign key (authentication_id) references authentication
alter table ticket add constraint FK1dsfvf5ogme7bo0qdhm8nti8c foreign key (from_station_id) references metro_station
alter table ticket add constraint FKff2cr2yf7muf2u2u65k4w16v8 foreign key (to_station_id) references metro_station
alter table ticket add constraint FK8tpqayef4gmi19hi0q436xqur foreign key (transaction_id) references transaction
alter table transaction add constraint FK5pvbln2scrqmjj1r7bukkws0l foreign key (authentication_id) references authentication
alter table transaction add constraint FK8jekxx6iujs52j44gqcfsnhup foreign key (linked_txn) references transaction
alter table user_detail add constraint FKnox7tn6aepargxacyq48k3r3h foreign key (authentication_id) references authentication