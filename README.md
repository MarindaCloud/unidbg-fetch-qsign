# unidbg-fetch-qsign

获取QQSign参数通过Unidbg，开放HTTP API。unidbg-fetch-sign最低从QQ8.9.33（不囊括）开始支持，TIM不支持。

> 多人使用请提高count值以提高并发量！！！

# 切记

 - 公共API具有高风险可能
 - 请使用与协议对应版本的libfekit.so文件
 - QSign基于Android平台，其它平台Sign计算的参数不同，不互通（例如：IPad）。
 - 不支持载入Tim.apk的so文件。

# 部署方法

## Jar部署

- 系统安装jdk或者jre，版本1.8或以上(仅1.0.3及更高版本，老版本要求jdk11)。如果报错找不到类，请尝试1.8或略靠近1.8的版本

- 解压后cd到解压目录，执行以下命令启动程序。<br>
```shell
bash bin/unidbg-fetch-qsign --host=0.0.0.0 --port=8080  --count=2 --library=txlib\8.9.63 --android_id=你的android_id
```
- 注意：你需要手动从apk安装包的`lib/arm64-v8a`目录中提取出[libfekit.so](txlib%2F8.9.63%2Flibfekit.so)、[libQSec.so](txlib%2F8.9.63%2FlibQSec.so)文件并存放至一个文件夹，然后使用`--library`指定该文件夹的`绝对路径`，结构例如：
> - your_dir<br>
>     - libfekit.so<br>
>     - libQSec.so<br>

> --library=`/home/your_dir`

- --host=监听地址
- --port=你的端口
 - --count=unidbg实例数量 (建议等于核心数) 【数值越大并发能力越强，内存占用越大】
 - --library=存放核心so文件的文件夹绝对路径
- [--dynamic] 可选参数：是否开启动态引擎, 默认关闭（加速Sign计算，有时候会出现[#52](https://github.com/fuqiuluo/unidbg-fetch-qsign/issues/52)）

## Docker部署

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

# 使用API

### 原始energy

```kotlin
# http://127.0.0.1:8080/custom_energy?salt=[SALT HEX]&data=[DATA]
```

### sign

```kotlin
# http://127.0.0.1:8080/sign?uin=[UIN]&qua=[QUA]&cmd=[CMD]&seq=[SEQ]&buffer=[BUFFER]
[UIN]：Bot的QQ号。实例值：“11451419198”
[QUA]：是手机QQ运行时，APP的某些信息，一般会在启动时得到。实例值：“V1_AND_SQ_8.9.63_4188_HDBM_T”
[CMD]：指令类型，在做什么的时候需要的sign就什么时候用，需要注意的是不仅登录的时候需要sign，发信息也需要带sign，所以CMD才有很多种。实例值：“wtlogin.login”
[SEQ]：意义不明的签名数字戳，看起来像时间戳。实例值：“1848698645”
[BUFFER]：密文，将byte数组转换为HEX发送。实例值：“0C099F0C099F0C099F”

因为有些时候密文会过长，导致超出get的长度上限，因此sign支持POST
content-type为application/x-www-form-urlencoded
正文和GET写法格式一样："uin=" + qq + "&qua=" + qua + "&cmd=" + cmd + "&seq=" + seq + "&buffer=" + DataUtils.byteArrayToHex(buffer)
```

### 登录包energy(tlv544)

下面这个只是个例子

```kotlin
# http://127.0.0.1:8080/energy?&version=[VERSION]&uin=[UIN]&guid=[GUID]&data=[DATA]
[VERSION]：注意！这里的VERSION指的不是QQ的版本号，而是SDK Version，可以在QQ安装包中找到此信息
[UIN]：Bot的QQ号。实例值：“11451419198”
[GUID]：密文，将byte数组转换为HEX发送，一般不会很长，不会超过GET上限。实例值：“0C099F0C099F0C099F”
[DATA]：实际上是"mode"，QQ在运行时会随机挑选一个作为加密题目，要根据题目算出密文结果才能通过认证。实例值：“810_d”
```
