package com.ctrip.zeus.service;

import com.ctrip.zeus.dal.core.AppDo;
import com.ctrip.zeus.dal.core.ServerDo;
import com.ctrip.zeus.dal.core.SlbDo;
import com.ctrip.zeus.model.entity.App;
import com.ctrip.zeus.model.entity.Server;
import com.ctrip.zeus.model.entity.Slb;
import org.unidal.dal.jdbc.DalException;

/**
 * @author:xingchaowang
 * @date: 3/7/2015.
 */
public interface DbSync {

    SlbDo sync(Slb slb) throws DalException;

    AppDo sync(App app);

    ServerDo sync(Server server);
}
