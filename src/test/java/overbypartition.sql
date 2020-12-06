select t7.vid,
   t7.province_name,t7.vehicle_type_kind,t7.color,t7.netin_time,t7.netin_dura,t7.load,
   t7.ttmile,t7.ttspd,t7.tttime,
   t7.highwaymile,t7.highspd,t7.countrymile,t7.countryspd,t7.provincemile,t7.provincespd,t7.countyothermile,t7.countyotherspd,
   t7.day_ave_spd,t7.night_ave_spd,
   t7.speed1,t7.speed2,t7.speed3,t7.speed4,
   t7.stop_citys,t7.stop_num,
   t7.citys,t7.num,
   t7.overspd_ct,t7.overspd_city_ct,t7.hw_ct,t7.nat_ct,t7.pro_ct,t7.cou_ct,
   t7.fatigue_ct,t7.fatigue_city_ct,
   add.inf,
   '20201010'
from
 (
 select t6.vid,
    t6.province_name,t6.vehicle_type_kind,t6.color,t6.netin_time,t6.netin_dura,t6.load,
    t6.ttmile,t6.ttspd,t6.tttime,
    t6.highwaymile,t6.highspd,t6.countrymile,t6.countryspd,t6.provincemile,t6.provincespd,t6.countyothermile,t6.countyotherspd,
    t6.day_ave_spd,t6.night_ave_spd,
    t6.speed1,t6.speed2,t6.speed3,t6.speed4,
    t6.stop_citys,t6.stop_num,
    t6.citys,t6.num,
    t6.overspd_ct,t6.overspd_city_ct,t6.hw_ct,t6.nat_ct,t6.pro_ct,t6.cou_ct,
    fatigue.fatigue_ct,fatigue.fatigue_city_ct
 from
 (
 select t5.vid,
    t5.province_name,t5.vehicle_type_kind,t5.color,t5.netin_time,t5.netin_dura,t5.load,
    t5.ttmile,t5.ttspd,t5.tttime,
    t5.highwaymile,t5.highspd,t5.countrymile,t5.countryspd,t5.provincemile,t5.provincespd,t5.countyothermile,t5.countyotherspd,
    t5.day_ave_spd,t5.night_ave_spd,
    t5.speed1,t5.speed2,t5.speed3,t5.speed4,
    t5.stop_citys,t5.stop_num,
    t5.citys,t5.num,
    overpsd.overspd_ct,overpsd.overspd_city_ct,overpsd.hw_ct,overpsd.nat_ct,overpsd.pro_ct,overpsd.cou_ct
 from
  (
  select t4.vid,
    t4.province_name,t4.vehicle_type_kind,t4.color,t4.netin_time,t4.netin_dura,t4.load,
    t4.ttmile,t4.ttspd,t4.tttime,
    t4.highwaymile,t4.highspd,t4.countrymile,t4.countryspd,t4.provincemile,t4.provincespd,t4.countyothermile,t4.countyotherspd,
    t4.day_ave_spd,t4.night_ave_spd,
    t4.speed1,t4.speed2,t4.speed3,t4.speed4,
    t4.stop_citys,t4.stop_num,
    pass.citys,pass.num
  from
   (
   select t3.vid,
  t3.province_name,t3.vehicle_type_kind,t3.color,t3.netin_time,t3.netin_dura,t3.load,
  t3.ttmile,t3.ttspd,t3.tttime,
  t3.highwaymile,t3.highspd,t3.countrymile,t3.countryspd,t3.provincemile,t3.provincespd,t3.countyothermile,t3.countyotherspd,
  t3.day_ave_spd,t3.night_ave_spd,
  t3.speed1,t3.speed2,t3.speed3,t3.speed4,
  tag.stop_citys,tag.stop_num
   from
   (
    select t2.vid,
  t2.province_name,t2.vehicle_type_kind,t2.color,t2.netin_time,t2.netin_dura,t2.load,
  t2.ttmile,t2.ttspd,t2.tttime,
  t2.highwaymile,t2.highspd,t2.countrymile,t2.countryspd,t2.provincemile,t2.provincespd,t2.countyothermile,t2.countyotherspd,
  t2.day_ave_spd,t2.night_ave_spd,
  spdrange.speed1,spdrange.speed2,spdrange.speed3,spdrange.speed4
  from
  (
   select
   t1.vid,
   t1.province_name,t1.vehicle_type_kind,t1.color,t1.netin_time,t1.netin_dura,t1.load,
   t1.ttmile,t1.ttspd,t1.tttime,
   t1.highwaymile,t1.highspd,t1.countrymile,t1.countryspd,t1.provincemile,t1.provincespd,t1.countyothermile,t1.countyotherspd,
   daynight.day_ave_spd,daynight.night_ave_spd
   from
   (
    select sam.vid,
    sam.province_name,sam.vehicle_type_kind,sam.color,sam.netin_time,sam.netin_dura,sam.load,
    mile.ttmile,mile.ttspd,mile.tttime,
    mile.highwaymile,mile.highspd,mile.countrymile,mile.countryspd,mile.provincemile,mile.provincespd,mile.countyothermile,mile.countyotherspd
    from
     (
     select mol.vid,mol.province_name,mol.vehicle_type_kind,mol.color,mol.netin_time,mol.netin_dura,mol.load
     from
     (
     select
     province_code,
     vid,
     province_name,
     vehicle_type_kind,
     color,
     netin_time,
     netin_dura,
     load,
     row_number() over(partition by province_code order by vid) as num
     from (
      select mile.vid,province_code,province_name,vehicle_type_kind,dve.color,
      from_unixtime(cast(enter_network_time/1000 as int),'yyyy/MM/dd HH:mm:ss') as netin_time,
      (substr(current_date,1,4)-from_unixtime(cast(enter_network_time/1000 as int),'yyyy'))*12+(substr(current_date,6,2)-from_unixtime(cast(enter_network_time/1000 as int),'MM'))
      as netin_dura,
      all_weight_drag/1000 as load
      from bigdata_warehouse.dws_vehicle_gathermile_d mile
      join bigdata_warehouse.d_vehicle dve
      on mile.vid = dve.vid
      where `date` = '20201010' and ttmile > 2 and ttmile >= highwaymile+countrymile+provincemile+countymile+othermile
      and source_code = '1100' and vehicle_transfer != '1' and province_code != ''
      ) a
     ) mol
    join
     (
     select province_code,count(1)*0.2 as ct
     from(
   select mile.vid,province_code,province_name,vehicle_type_kind,dve.color,
   from_unixtime(cast(enter_network_time/1000 as int),'yyyy/MM/dd HH:mm:ss') as netin_time,
   (substr(current_date,1,4)-from_unixtime(cast(enter_network_time/1000 as int),'yyyy'))*12+(substr(current_date,6,2)-from_unixtime(cast(enter_network_time/1000 as int),'MM'))
   as netin_dura,
   all_weight_drag/1000 as load
   from bigdata_warehouse.dws_vehicle_gathermile_d mile
   join bigdata_warehouse.d_vehicle dve
   on mile.vid = dve.vid
   where `date` = '20201010' and ttmile > 2 and ttmile >= highwaymile+countrymile+provincemile+countymile+othermile
   and source_code = '1100' and vehicle_transfer != '1' and province_code != ''
   ) b
   group by province_code
     ) den
     on mol.province_code = den.province_code
     where mol.num <= den.ct
     ) sam
    left join
     (
     select
     vid,
     ttmile,
     ttmile/(tttime/60/60) as ttspd,
     tttime/60/60 as tttime,
     highwaymile,
     highwaymile/(highwaytime/60/60) as highspd,
     countrymile,
     countrymile/(countrytime/60/60) as countryspd,
     provincemile,
     provincemile/(provincetime/60/60) as provincespd,
     countymile+othermile as countyothermile,
     countymile+othermile/((countytime+othertime)/60/60) as countyotherspd
     from bigdata_warehouse.dws_vehicle_gathermile_d
     where `date` = '20201010' and ttmile > 2 and ttmile >= highwaymile+countrymile+provincemile+countymile+othermile
     ) mile
     on sam.vid = mile.vid
   ) t1
   left join
    (
    select vid,
     ((if(mileage_5<0,0,mileage_5)
     +if(mileage_6<0,0,mileage_6)
     +if(mileage_7<0,0,mileage_7)
     +if(mileage_8<0,0,mileage_8)
     +if(mileage_9<0,0,mileage_9)
     +if(mileage_10<0,0,mileage_10)
     +if(mileage_11<0,0,mileage_11)
     +if(mileage_12<0,0,mileage_12)
     +if(mileage_13<0,0,mileage_13)
     +if(mileage_14<0,0,mileage_14)
     +if(mileage_15<0,0,mileage_15)
     +if(mileage_16<0,0,mileage_16)
     +if(mileage_17<0,0,mileage_17)
     +if(mileage_18<0,0,mileage_18)
     +if(mileage_19<0,0,mileage_19)
     +if(mileage_20<0,0,mileage_20)
     +if(mileage_21<0,0,mileage_21))
     /1000)
     /
     ((if(drivetime_5<0,0,drivetime_5)
     +if(drivetime_6<0,0,drivetime_6)
     +if(drivetime_7<0,0,drivetime_7)
     +if(drivetime_8<0,0,drivetime_8)
     +if(drivetime_9<0,0,drivetime_9)
     +if(drivetime_10<0,0,drivetime_10)
     +if(drivetime_11<0,0,drivetime_11)
     +if(drivetime_12<0,0,drivetime_12)
     +if(drivetime_13<0,0,drivetime_13)
     +if(drivetime_14<0,0,drivetime_14)
     +if(drivetime_15<0,0,drivetime_15)
     +if(drivetime_16<0,0,drivetime_16)
     +if(drivetime_17<0,0,drivetime_17)
     +if(drivetime_18<0,0,drivetime_18)
     +if(drivetime_19<0,0,drivetime_19)
     +if(drivetime_20<0,0,drivetime_20)
     +if(drivetime_21<0,0,drivetime_21))
     /3600) as day_ave_spd,
     ((if(mileage_0<0,0,mileage_0)
     +if(mileage_1<0,0,mileage_1)
     +if(mileage_2<0,0,mileage_2)
     +if(mileage_3<0,0,mileage_3)
     +if(mileage_4<0,0,mileage_4)
     +if(mileage_22<0,0,mileage_22)
     +if(mileage_23<0,0,mileage_23))
     /1000)
     /
     ((if(drivetime_0<0,0,drivetime_0)
     +if(drivetime_1<0,0,drivetime_1)
     +if(drivetime_2<0,0,drivetime_2)
     +if(drivetime_3<0,0,drivetime_3)
     +if(drivetime_4<0,0,drivetime_4)
     +if(drivetime_22<0,0,drivetime_22)
     +if(drivetime_23<0,0,drivetime_23))
     /3600) as night_ave_spd
    from bigdata_warehouse.dws_vehicle_gathermile_hour
    where `date` = '20201010'
    ) daynight
    on daynight.vid = t1.vid
  ) t2
    left join
  (
  select vid,speed1,speed2,speed3,speed4
  from bigdata_warehouse.dws_vehicle_speed_range
  where dt = '20201010'
  ) spdrange
  on t2.vid = spdrange.vid
   ) t3
   left join
   (
   select vid,concat_ws('-',collect_set(substr(city_code,1,4))) as stop_citys,count(city_code) as stop_num
   from bigdata_warehouse.dwd_vehicle_stop_point
   where `date` = '20201010' and mark = '1' and duration/3600000 > 4 and start_utc/1000 >= '20201010'
   group by vid
   ) tag
   on t3.vid = tag.vid
   ) t4
  left join
  (
  select vid,concat_ws('-',collect_list(substr(city_code,1,4))) as citys,count(city_code) as num
  from(
  select vid,city_code,enter_time
  from bigdata_warehouse.dwd_vehicle_pass_city
  where `date` = '20201010'
  distribute by vid
  sort by vid,enter_time) a
  group by vid
  ) pass
  on t4.vid = pass.vid
  ) t5
 left join
  (
  select vid,sum(hw_num+nat_num+pro_num+cou_num) as overspd_ct,
  concat_ws('-',collect_list(concat_ws('-',substr(start_city_code,1,4),cast(hw_num+nat_num+pro_num+cou_num as string)))) as overspd_city_ct,
  sum(hw_num) as hw_ct,sum(nat_num) as nat_ct,sum(pro_num) as pro_ct,sum(cou_num) as cou_ct
  from bigdata_warehouse.dws_vehicle_overspd_city_d
  where dt = '20201010'
  group by vid
  ) overpsd
 on t5.vid = overpsd.vid
 ) t6
 left join
 (
  select totail.vid,fatigue_ct,fatigue_city_ct
  from
  (
    select vid,count(*) as fatigue_ct
    from bigdata_warehouse.dwd_vehicle_fatigue
    where `date` = '20201010' and ftype = 'type4'
    group by vid
  ) totail
  left join
  (
    select vid,concat_ws('-',collect_list(concat_ws('-',substr(start_city_code,1,4),cast(num as string)))) as fatigue_city_ct
    from bigdata_warehouse.dws_vehicle_fatigue_city_d
    where dt = '20201010'
    group by vid
  ) city_ct
  on totail.vid = city_ct.vid
 ) fatigue
 on t6.vid = fatigue.vid
 ) t7
left join
  (
   select vid,concat_ws('-',collect_list(concat_ws('-',substr(city_code,1,4),cast(ct as string)))) as inf
   from
   (
     select
      pass.vid,
      pass.city_code,
      enter_time,
      sum(case when end_utc >= enter_time and end_utc <= out_time then 1 else 0 end) as ct
     from
       (
       select vid,city_code,enter_time,out_time
       from bigdata_warehouse.dwd_vehicle_pass_city
       where `date` = '20201010'
       ) pass
    left join
      (
      select vid,city_code,start_utc,end_utc
      from bigdata_warehouse.dwd_vehicle_stop_point
      where `date` = '20201010' and mark = '1' and duration/3600000 > 4  and start_utc/1000 >= '20201010'
      ) lstop
    on pass.vid = lstop.vid and pass.city_code = lstop.city_code
    group by pass.vid,pass.city_code,enter_time
    sort by vid,enter_time
  ) asd
  group by vid
  ) add
on t7.vid = add.vid