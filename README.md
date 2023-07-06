获取QQSign参数通过Unidbg，开放HTTP API。unidbg-fetch-sign最低从QQ8.9.33（不囊括）开始支持，TIM不支持。

# 切记

 - 公共API具有高风险可能
 - 请使用与协议对应版本的libfekit.so文件
 - QSign基于Android平台，其它平台Sign计算的参数不同，不互通（例如：IPad）。
 - 不支持载入Tim.apk的so文件。

# 部署方法

## Jar部署

- 系统安装jdk或者jre，版本1.8或以上。如果报错找不到类，请尝试1.8或略靠近1.8的版本

- 解压后cd到解压目录，配置config.json文件。<br>

其中```protocol```中的参数可以从[protocol-versions](https://github.com/RomiChan/protocol-versions)获取！

```json5
{ // 复制这里的话，请把注释删除
  "server": {
    "host": "0.0.0.0",
    "port": 8080
  },
  // 注册实例的密钥
  "key": "114514",
  // 实例重载间隔（目前没有用）
  // i>=20 i<=50
  "reload_interval": 40, 
  "protocol": {
    "qua": "V1_AND_SQ_8.9.63_4194_YYB_D",
    // version和code可以从qua中提取
    "version": "8.9.63", 
    "code": "4194"
  },
  "unidbg": {
    // 启用Dynarmic，它是一个开源的动态ARM指令集模拟器
    // 有时候会出现https://github.com/fuqiuluo/unidbg-fetch-qsign/issues/52
    "dynarmic": false,
    "unicorn": true,
    "debug": false
  }
}
```


```shell
bash bin/unidbg-fetch-qsign --basePath=txlib/8.9.63
```
- 注意：你需要手动从apk安装包的`lib/arm64-v8a`目录中提取出[libfekit.so](txlib%2F8.9.63%2Flibfekit.so)、[libQSec.so](txlib%2F8.9.63%2FlibQSec.so)文件并存放至一个文件夹，然后使用`--basePath`指定该文件夹的`绝对路径`，结构例如：
> - your_dir<br>
>     - libfekit.so<br>
>     - libQSec.so<br>
>     - config.json<br>
>     - dtconfig.json<br>

> ```dtconfig.json```是FEBound.java内数据的热更新版本，每个版本的QQ不同，可自行从```com.tencent.mobileqq.dt.model.FEBound```逆向得到。

> --basePath=`/home/your_dir`

## Docker部署 (待修正)

[xzhouqd/qsign](https://hub.docker.com/r/xzhouqd/qsign)

## docker-compose部署

直接使用openjdk11启动服务

```yaml
version: '2'

services:
  qsign:
    image: openjdk:11.0-jdk
    environment:
      TZ: Asia/Shanghai
    restart: always
    working_dir: /app
    # 按需修改相关参数
    command: bash bin/unidbg-fetch-qsign --port=8080 --count=1 --library=txlib/8.9.63 --android_id=someandroidid
    volumes:
      # 当前目录放置qsign的解压包
      - ./unidbg-fetch-qsign:/app
      # 当前目录放置txlib
      - ./txlib:/app/txlib
    ports:
      # 按需调整宿主机端口
      - 8901:8080
```

# 你可能需要的项目

- [fix-protocol-version](https://github.com/cssxsh/fix-protocol-version)：基于**mirai**的qsign api对接。

# 使用API

## [初始化QSign&刷新token](https://github.com/fuqiuluo/unidbg-fetch-qsign/blob/master/refresh_token/README.md)

### 原始energy

```kotlin
# http://127.0.0.1:8080/custom_energy?uin=[QQ]&salt=[SALT HEX]&data=[DATA]
```
| 参数名  |意义|例子|
|------|-----|-----|
| UIN  |Bot的QQ号|114514|

> 非专业人员勿用。

### sign

```kotlin
# http://127.0.0.1:8080/sign?uin=[UIN]&qua=[QUA]&cmd=[CMD]&seq=[SEQ]&buffer=[BUFFER]
```
|参数名|意义|例子|
|-----|-----|-----|
|UIN|Bot的QQ号|114514|
|QUA|QQ User-Agent，与QQ版本有关|V1_AND_SQ_8.9.63_4188_HDBM_T|
|CMD|指令类型，CMD有很多种，目前登录、发信息均需要sign|wtlogin.login|
|SEQ|数据包序列号，用于指示请求的序列或顺序。它是一个用于跟踪请求的顺序的数值，确保请求按正确的顺序处理|2333|
|BUFFER|数据包包体，不需要长度，将byte数组转换为HEX发送|020348010203040506|

<details>
<summary>POST的支持</summary>

如果buffer过长，会超出get请求方式的长度上限，因此sign的请求也支持POST的方式。

请求头 `Content-Type: application/x-www-form-urlencoded`

POST的内容："uin=" + uin + "&qua=" + qua + "&cmd=" + cmd + "&seq=" + seq + "&buffer=" + buffer
</details>

### 登录包energy(tlv544)

下面这个只是个例子

```kotlin
# http://127.0.0.1:8080/energy?version=[VERSION]&uin=[UIN]&guid=[GUID]&data=[DATA]
```

|参数名|意义|例子|
|-----|-----|-----|
|VERSION|**注意！**这里的VERSION指的**不是QQ的版本号，而是SDK Version**，可以在QQ安装包中找到此信息|6.0.0.2549|
|UIN|Bot的QQ号|114514|
|GUID|登录设备的GUID，将byte数组转换为HEX发送，必须是32长度的HEX字符串|ABCDABCDABCDABCDABCDABCDABCDABCD|
|DATA|QQ发送登录包的CmdId和SubCmdId，例子中810是登陆CmdId，9是SubCmdId|810_9|

# 其他
- 由于项目的特殊性，我们可能~~随时删除本项目~~且不会做出任何声明

# 奇怪的交际援助

 - 昵称：**[咖啡]**  QQ：1456****68
