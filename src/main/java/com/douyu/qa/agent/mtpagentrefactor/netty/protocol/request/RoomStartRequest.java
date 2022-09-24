package com.douyu.qa.agent.mtpagentrefactor.netty.protocol.request;

import com.douyu.qa.agent.mtpagentrefactor.netty.protocol.BaseProtocol;
import com.douyu.qa.agent.mtpagentrefactor.netty.protocol.TypeObjectEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author zhuifeng
 */
@Setter
@Getter
@ToString
public class RoomStartRequest extends BaseProtocol {

    private Long id;


    private Long task_id;


    private String device_id;


    private String platform;


    private Date create_time;


    private Date update_time;


    private Long prf_pl_ro_0;


    private Long prf_pl_request_302_0;


    private String pm;


    private Long is_back;


    private Long sd;


    private Long prf_pl_render_0;


    private Long prf_pl_ps_0;


    private Long is_ma;


    private Long prf_pl_rt_0;


    private String clar;


    private Long prf_pl_ui_0;


    private String service_t;


    private Long prf_pl_data_0;


    private Long prf_pl_frame_init_0;


    private Long prf_pl_p2p_0;


    private String type;


    private Long prf_pl_dns1_0;


    private String line;


    private String isp;


    private Long prf_pl_connect1_0;


    private Long prf_pl_dns2_0;


    private Long prf_pl_ss_0;


    private String ov;


    private Long prf_pl_connect2_0;


    private Long prf_pl_co_0;


    private Long prf_pl_ho_0;


    private Long s_type;

    @Override
    public Integer getProtocol() {
        return TypeObjectEnum.ROOM_START_ANDROID.getCode();
    }
}