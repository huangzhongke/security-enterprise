/**
 * Copyright (c) 2016-2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.websocket.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.websocket.Session;

/**
 * WebSocket连接数据
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@AllArgsConstructor
public class WebSocketData {
    private Long userId;
    private Session session;
}