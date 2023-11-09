# fork版本 非原创


获取QQSign参数通过Unidbg，开放HTTP API。unidbg-fetch-sign最低从QQ8.9.33（不囊括）开始支持，TIM不支持。

# 切记

 - 公共API具有高风险可能
 - 请使用与协议对应版本的libfekit.so文件
 - QSign基于Android平台，其它平台Sign计算的参数不同，不互通（例如：IPad）。
 - 不支持载入Tim.apk的so文件。

# 部署方法

**[Wiki](https://github.com/fuqiuluo/unidbg-fetch-qsign/wiki)**

# 你可能需要的项目

- [fix-protocol-version](https://github.com/cssxsh/fix-protocol-version)：基于**mirai**的qsign api对接。

# 使用API

## [初始化QSign&刷新token](https://github.com/fuqiuluo/unidbg-fetch-qsign/blob/master/refresh_token/README.md)

### 原始energy

```kotlin
# http://host:port/custom_energy?uin=[QQ]&salt=[SALT HEX]&data=[DATA]
```
| 参数名 | 意义      | 例子     |
|-----|---------|--------|
| UIN | Bot的QQ号 | 114514 |

> 非专业人员勿用。

### sign

```kotlin
# http://host:port/sign?uin=[UIN]&qua=[QUA]&cmd=[CMD]&seq=[SEQ]&buffer=[BUFFER]
```
| 参数名    | 意义                                                | 例子                          |
|--------|---------------------------------------------------|-----------------------------|
| UIN    | Bot的QQ号                                           | 114514                      |
| QUA    | QQ User-Agent，与QQ版本有关                             | V1_AND_SQ_8.9.68_4264_YYB_D |
| CMD    | 指令类型，CMD有很多种，目前登录、发信息均需要sign                      | wtlogin.login               |
| SEQ    | 数据包序列号，用于指示请求的序列或顺序。它是一个用于跟踪请求的顺序的数值，确保请求按正确的顺序处理 | 2333                        |
| BUFFER | 数据包包体，不需要长度，将byte数组转换为HEX发送                       | 020348010203040506          |

<details>
<summary>POST的支持</summary>

如果buffer过长，会超出get请求方式的长度上限，因此sign的请求也支持POST的方式。

请求头 `Content-Type: application/x-www-form-urlencoded`

POST的内容："uin=" + uin + "&qua=" + qua + "&cmd=" + cmd + "&seq=" + seq + "&buffer=" + buffer
</details>

### 登录包energy(tlv544)

下面这个只是个例子

```kotlin
# http://host:port/energy?version=[VERSION]&uin=[UIN]&guid=[GUID]&data=[DATA]
```

| 参数名     | 意义                                                           | 例子                               |
|---------|--------------------------------------------------------------|----------------------------------|
| VERSION | **注意！**这里的VERSION指的**不是QQ的版本号，而是SDK Version**，可以在QQ安装包中找到此信息 | 6.0.0.2549                       |
| UIN     | Bot的QQ号                                                      | 114514                           |
| GUID    | 登录设备的GUID，将byte数组转换为HEX发送，必须是32长度的HEX字符串                     | ABCDABCDABCDABCDABCDABCDABCDABCD |
| DATA    | QQ发送登录包的CmdId和SubCmdId，例子中810是登陆CmdId，9是SubCmdId             | 810_9                            |

# 其他
- 由于项目的特殊性，我们可能~~随时删除本项目~~且不会做出任何声明

# 奇怪的交际援助

 - 昵称：**[咖啡]**  QQ：1456****68
 - 昵称：**RinsToln** QQ：339***8297302
 - 昵称：**菩提** QQ：919***595
 - 
