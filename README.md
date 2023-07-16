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
  // 启用自动注册实例（需要1.1.4及以上版本才会生效）
  "auto_register": true,
  // 实例重载间隔（目前没有用）
  // i>=20 i<=50
  "reload_interval": 40, 
  "protocol": {
    "qua": "V1_AND_SQ_8.9.68_4264_YYB_D",
    // version和code可以从qua中提取
    "version": "8.9.68", 
    "code": "4264"
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
bash bin/unidbg-fetch-qsign --basePath=txlib/8.9.68
```
- 注意：你需要手动从apk安装包的`lib/arm64-v8a`目录中提取出[libfekit.so](txlib%2F8.9.63%2Flibfekit.so)、[libQSec.so](txlib%2F8.9.63%2FlibQSec.so)文件并存放至一个文件夹，然后使用`--basePath`指定该文件夹的`绝对路径`，结构例如：
> - your_dir<br>
>     - libfekit.so<br>
>     - libQSec.so<br>
>     - config.json<br>
>     - dtconfig.json<br>

> ```dtconfig.json```是FEBound.java内数据的热更新版本，每个版本的QQ不同，可自行从```com.tencent.mobileqq.dt.model.FEBound```逆向得到。

> --basePath=`/home/your_dir`

## Docker部署

[Dockerhub: xzhouqd/qsign](https://hub.docker.com/r/xzhouqd/qsign)

此Docker image相关提问请到：[xzhouqd/unidbg-fetch-qsign](https://github.com/XZhouQD/unidbg-fetch-qsign) 提交issue

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
    command: bash bin/unidbg-fetch-qsign --basePath=txlib/8.9.68
    volumes:
      # 当前目录放置qsign的解压包
      - ./unidbg-fetch-qsign:/app
      # 当前目录放置txlib
      - ./txlib:/app/txlib
    ports:
      # 按需调整宿主机端口
      - 8901:8080
```



## Linux 部署（Ubuntu）


### 环境配置

```bash
环境配置：
x86
Ubuntu 22.04.1
openjdk version "19.0.2" 

在root家目录操作
```

### 下载 unidbg-fetch-qsign 的 Releases，解压

这里以1.1.3为例，记得改下载连接，慢的话可以传上去或者用代理

https://github.com/fuqiuluo/unidbg-fetch-qsign/releases

```bash
wget https://github.com/fuqiuluo/unidbg-fetch-qsign/releases/download/1.1.0/unidbg-fetch-qsign-1.1.3.zip

# 没装unzip的话
apt install unzip
# 解压，注意文件名
unzip unidbg-fetch-qsign-1.1.3.zip
```

### 修改配置文件，初次运行

- 检查 java

```bash
java -version

# 没装的话
apt install openjdk-19-jdk
```

- 查看端口占用

```bash
netstat -lntp
```
- 确定配置好config.json文件后尝试启动

```bash
/root/unidbg-fetch-qsign-1.1.3/bin/unidbg-fetch-qsign --basePath=/root/unidbg-fetch-qsign-1.1.3/txlib/8.9.68
```

测试可以正常运行，ctrl+c结束

### 后台运行与开机启动 （通过 systemd）

- 新建文件

```bash
vi /etc/systemd/system/qsign.service

#========在qsign.service文件中输入以下内容========
#========注意更改Service参数，要用绝对路径========
[Unit]
Description=unidbg-fetch-qsign
After=network.target

[Service]
ExecStart=这里输入之前前台运行测试成功的命令

[Install]
WantedBy=multi-user.target
#================

```

- 重载与启动

```bash

#重载服务，每次修改都要
sudo systemctl daemon-reload
#启动qsign
sudo systemctl start qsign

#查看端口情况，可见已在指定的port开启服务
netstat -lntp

#以下数条可按需执行
#启动
sudo systemctl start qsign
#停止
sudo systemctl stop qsign
#重启
sudo systemctl restart qsign
#设置开机启动
sudo systemctl enable qsign
#禁用开机启动
sudo systemctl disable qsign
#查看运行状态
sudo systemctl status qsign

```

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
