# 接口文档

## 0
* baseurl : /api/
* 返回形式：
  ```
  {
  "status":200,
  "message":"OK",
  "data":...
  }
  ```
* 对于动态URL使用如下形式描述
  ```
  GET /contracts/{contractId}
  ```
  具体URL为`/contracts/123`  
  如上表示获得id为123的合同
* 对于需要授权的接口（例如获取已签署的合同、获取登录设备等），会在url中附加token参数。例如：
  ```
  GET /contracts/100?token=12345
  ```

## 具体请求

### 登录授权（PKI授权系统）

* #### 获取授权码
  获取一串数字或字符，用于生成二维码兑换登录token
  ```
  GET /auth/code
  ```
  ##### response
  ```
  {
    "status":200,
    "message":"success",
    "data":"XXXXXXXXX"//授权码
  }
  ```
* #### 检测授权码状态
  **该请求为websocket连接**
  ```
  GET /auth/codeStatus?code=xyz
  ```
  ##### params
  
  |Name|Type|Description|
  |---|---|---|
  |code|string|要探测的授权码|
  ##### 规则
  * client不主动发送消息，只接受服务器消息
  * 服务端设置过期时间（3分钟或其他），过期后手动关闭socket连接，此时客户端将视为过期
  * 其他意外情况导致socket的关闭，客户端也可能视为过期
  * 如果在有效期内二维码成功被验证，则服务端发送形如下的消息来传递token：
    ```
    "status":200,
    "message":"success",
    "data":"xxxxxxxxx"//data为字符串，表示成功扫码取得的token
    ```
  * 成功验证授权码后，服务器最好**不要主动关闭**socket连接，而是等客户端关闭
  
### 合同(contract)
合同暂时规定为两方合同（甲方和乙方），甲方为合同的发起者。双方都有修改合同内容的权限
#### 关于对合同的操作
共有以下几种操作：
* 下载原合同，原合同指合同的源文件，不包含签名信息
* 接受合同，表明用户已详细阅读合同内容，并对合同内容无异议，确认后**不可再次更改**
* 拒绝合同，表明用户不同意合同内容，此时服务器直接删除合同相关数据
* 修改合同，表明用户已详细阅读合同内容，并对合同作出修改（上传合同文件并覆盖），此
* 签名，只有双方都同意后才能签名。
* 验证本地文件，通过上传本地文件验证本地合同文件的真实性，此时两方已完成签名
* 下载签名后的合同，此时两方已完成签名

**注**：同一时刻，合同接受或拒绝的权限只能由某一方拥有，另一方只能修改。每一次合同的修改，都表示合同接受、拒绝权限的转移。

#### 关于合同状态
##### 字段：type  
合同状态共有以下几种状态：
1. 待确认：合同内容有变化。此时可以执行*接受、拒绝、修改*。
2. 已修改并待对方确认。此时可以多次执行*修改*操作
3. 已确认且对方也已确认。此时可执行签名操作。
4. 已签名并待对方完成签名。
5. 已签名且对方也已签名，此时合同签署流程执行完毕。

**注**：当甲方成功创建合同后，甲方自动进入*状态2*，乙方自动进入*状态1*  
**注**：合同在签署期间的任何阶段都可以下载
**注**：同一时刻，双方的合同状态*可能*是不同的。


* #### 获取该用户全部合同
  ```
  GET /contracts
  ```
  #### response
  ```
  {
  "status":200,
  "message":"success",
  "data":[
    {
      id:156123,//合同ID
      type: 0,//数字，表示上文描述的合同状态
      title: '二手房买卖合同2018',
      file: {
          filename: 'test.pdf',
          link: 'http://downloadfile.orz/test.pdf',
          size: 128//文件大小，单位KB
      },
      lastModified: 11123155213//合同文件最后修改时间，时间戳,
      partAName:'张全蛋'，
      partAIDCard:'32423424',
      partBName:'李华'，
      partBIDCard:'32423424'
    },
    ......
  ]
  }
  ```
* #### 使用合同ID获取某一份合同
  ```
  GET /contracts/{contractId}
  ```
  #### response
  ```
  {
  "status":200,
  "message":"success",
  "data":{
    id:156123,//合同ID
    type: 0,//数字，表示上文描述的合同状态
    title: '二手房买卖合同2018',
    file: {
        filename: 'test.pdf',
        link: 'http://downloadfile.orz/test.pdf',
        size: 128//文件大小，单位KB
    },
    lastModified: 11123155213//合同文件最后修改时间，时间戳,
    partAName:'张全蛋'，
    partAIDCard:'32423424',
    partBName:'李华'，
    partBIDCard:'32423424'
  }
  ```
* #### 创建合同
  ```
  POST /contracts/create
  ```
  ##### input
  |Name|Type|Description|
  |---|---|---|
  |file|file|合同文件|
  |title|string|合同标题|
  |partBName|string|乙方姓名|
  |partBIDCard|string|乙方身份证件号码|

* #### 修改合同
  **降低复杂度，暂时只能修改合同文件**
  ```
  POST /contracts/{contractId}/update
  ```
  ##### input
  |Name|Type|Description|
  |---|---|---|
  |file|file|合同文件|


* #### 同意合同内容
  ```
  POST /contracts/{contractId}/accept
  ```

* #### 拒绝合同内容
  ```
  POST /contracts/{contractId}/decline
  ```

* #### 获取签名授权码
  ```
  GET /contracts/{contractId}/signCode
  ```
  #### response
  ```
  {
  "status":200,
  "message":"success",
  "data":"sdfsdf"//授权码
  
* #### 检测签名授权码状态
  需要等待手机加密完成并上传，**该请求为websocket连接**
  ```
  GET /signCodeStatus?code=xyz
  ```
  ##### params
  
  |Name|Type|Description|
  |---|---|---|
  |code|string|要探测的签名授权码|
  ##### 规则
  * client不主动发送消息，只接受服务器消息
  * 服务端设置过期时间（3分钟或其他），过期后手动关闭socket连接，此时客户端将视为过期
  * 其他意外情况导致socket的关闭，客户端也可能视为过期
  * 如果在有效期内二维码成功被验证，则服务端发送形如下的消息：
    ```
    "status":200,
    "message":"success"
    ```
  * 成功验证授权码后，服务器最好**不要主动关闭**socket连接，而是等客户端关闭

  
### 日志(log)

* #### 获取该用户全部日志
  ```
  GET /logs
  ```
  #### response
  ```
  {
  "status":200,
  "message":"success",
  "data":[
    {
      serial: 'SDFSDXV-345',//设备唯一ID
      timestamp:15656554//时间戳
    },
    ......
  ]
  }
  ```
