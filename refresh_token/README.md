# 初始化Sign

 - 当首次调用```/sign```的时候会有类似以下返回

```json5
{
    "code": 0,
    "msg": "success",
    "data": {
        "token": "xxx",
        "extra": "xxx",
        "sign": "xxx",
        "o3did": "xxx",
        "requestCallback": [
            {
                "cmd": "trpc.o3.ecdh_access.EcdhAccess.SsoSecureA2Establish",
                "body": "xxx",
                "callbackId": 0
            },
            {
                "cmd": "trpc.o3.ecdh_access.EcdhAccess.SsoSecureA2Establish",
                "body": "xxx",
                "callbackId": 1
            },
            {
                "cmd": "trpc.o3.ecdh_access.EcdhAccess.SsoSecureA2Establish",
                "body": "xxx",
                "callbackId": 2
            }
        ]
    }
}
```

- 你需要发送```requestCallback```内的Packet（部分Packet需要签sign或者登录后才能发送），并携带callback_id提交返回包给API。

```http request
http://your.host/submit?uin=[QQ]&cmd=[CMD]&callback_id=[CALLBACK_ID]&buffer=[BUFFER]
```

> WARN: 其中```buffer```参数无需携带4字节（32bit）的长度。

# 刷新Token

sign的token会过期（过期时间在1小时左右，建议每隔30~40分钟请求刷新token）

- 请求方法```/request_token?uin=[QQ]```获取刷新token包