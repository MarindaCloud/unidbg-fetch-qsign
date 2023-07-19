# 初始化Sign

 - 执行```/register```请求，注册对应QQ实例进系统。

```kotlin
http://host:port/register?uin=[QQ]&android_id=[ANDROID_ID]&guid=[GUID]&qimei36=[QIMEI36]&key=[KEY]
```

如果是第一次注册实例则会出现以下返回

```json5
{
    "code": 0,
    "msg": "Instance loaded successfully.",
    "data": ""
}
```

如果这个QQ已经注册实例了，但是又执行一次，则会以下返回

```json5
{
    "code": 0,
    "msg": "The QQ has already loaded an instance, so this time it is deleting the existing instance and creating a new one.",
    "data": ""
}
```

未注册实例请求API会出现以下返回

```json5
{
    "code": 1,
    "msg": "Uin is not registered.",
    "data": "/sign?uin=xxx&qua=V1_AND_SQ_8.9.63_4188_HDBM_T&cmd=xxx&seq=xxx&buffer=xxx"
}
```

### 关于自动注册实例的说明

 - 如果不想使用```/register```注册实例，想直接使用```/energy```和```/sign```请求，可以在config.json修改auto_register参数为true，启用自动注册实例功能。
 - 此外，需要注意的是，在首次请求```/sign```或```/energy```需提交额外的参数```android_id```和```guid```，后续请求即可省略。

# 联网更新Token

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

```kotlin
http://host:port/submit?uin=[QQ]&cmd=[CMD]&callback_id=[CALLBACK_ID]&buffer=[BUFFER]
```

> WARN: 其中```buffer```参数无需携带4字节（32bit）的长度。

# 刷新Token

sign的token会过期（过期时间在1小时左右，建议每隔30~40分钟请求刷新token）

```kotlin
http://host:port/request_token?uin=[QQ]
```

# 销毁实例

可以主动销毁已经注册的实例，使用本API至少需要1.1.6版本及以上的支持。

```kotlin
http://host:port/destroy?uin=[QQ]&key=[key]
```

成功销毁实例返回

```json5
{
    "code": 0,
    "msg": "Instance destroyed successfully.",
    "data": ""
}
```

如果该实例未注册则返回

```json5
{
    "code": 1,
    "msg": "Instance does not exist.",
    "data": "failed"
}
```
