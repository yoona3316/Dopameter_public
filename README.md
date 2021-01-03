# Dopameter

Android Dopameter는 백그라운드에서 자동으로 유저 데이터를 수집합니다. 현재까지 모으는 데이터는 블루투스, 전화, 위치, 알림, 알림 랭크, 배터리 상태, screen On/Off, 센서(light, proximity), 문자, 앱 사용 입니다. 

코드는 크게 세 부분으로 구성되어 있습니다. 

첫째, models/ 하위의 모듈들은 수집하는 사용자 데이터의 모델과 모델의 리스트를 정의합니다. 이때 모든 모델과 모델의 리스트는 같은 인터페이스를 상속합니다. 자세한 모델 필드 스펙은 [Models](#Model) 을 참고하세요.

둘째, trackers/ 하위의 모듈들은 실제 데이터를 수집하는 모듈들입니다. 트래커가 작동하는 시간은 모듈마다 다르며, 크게 5분마다 작동하는 모듈과 broadcast가 수신되었을 때마다 작동하는 모듈로 나뉩니다. 자세한 분류는 [Trackers](#Trackers)을 참고하세요.

끝으로 이외 코드들은 tracker 를 시작하거나, 앱의 ui를 구성하는 데 사용됩니다. 자세한 코드 설명은 아래 [Others](#Others)에서 참고하세요.



## 목차

* [Target](#Target)
* [Model](#Model)
* [Tracker](#Tracker)
* [Others](#Others)
* [Permissions](#Permissions)
* [Library](#Library)



## Target

```
minSdkVersion 26
targetSdkVersion 29
```



## Model

위치: sensors/models

* [ModelAdapterInterface](#ModelAdapterInterface)
* [ActivityRecognitionModel](#ActivityRecognitionModel)
* [BleModel](#BleModel)
* [BluetoothModel](#BluetoothModel)
* [CallModel](#CallModel)
* [InstalledAppModel](#IntalledAppModel)
* [LightModel](#LightModel)
* [LocationDeviceModel](#LocationDeviceModel)
* [NotificationModel](#NotificationModel)
* [NotificationRankingModel](#NotificationRankingModel)
* [PackageModel](#PackageModel)
* [PowerModel](#PowerModel)
* [ProximityModel](#ProximityModel)
* [ScreenLockModel](#ScreenLockModel)
* [ScreenModel](#ScreenModel)
* [SMSModel](#SMSModel)
* [UsageStatsModel](#UsageStatsModel)



### ModelAdapterInterface

>  models/ 하위의 모든 모듈이 상속하는 ModelInterface와 ModelAdapterInterface를 정의합니다. 

* ModelInterface

  Model의 필드를 정의하기 위한 인터페이스입니다. 데이터 필드를 정의하는 모든 모델은 이 인터페이스를 상속합니다.

  필드로 time을 정의합니다. 

  이외 파일 저장 및 firestore 저장을 위한 함수들이 정의되어 있습니다. 

* ModelAdapterInterface

  Model의 리스트를 정의하기 위한 인터페이스입니다. 모든 modelListAdapter는 이 인터페이스를 상속합니다. 

  ModelInterface를 상속하는 모델들의 리스트를 가지고 있습니다.

   리스트에 데이터를 추가하는 함수(add)와 리스트의 모든 데이터를 삭제하는 함수(clear)가 정의되어 있습니다. 

### ActivityRecognitionModel

> Google Api 이용하여 사용자의 활동 저장

```kotlin
// 정보 전송 시점을 기준으로 한 unix 시간  -> 업데이트 예정
override var time: Long?

// 기기 부팅 시점 기준, 이벤트가 측정된 elapsedRealtimeInNanos
val elapsedTimeNanos: Long

// 인식된 activity. VEHICLE, BYCYCLE, FOOT, RUNNING, STILL, WALKING 중 하나. 자세한 정보는 DetectedActivity 문서 참고
val activity: Int?

// 인식된 transition. ENTER, EXIT 중 하나. 자세한 정보는 ActivityTransition 문서 참고
val transition: Int?
```



### BleModel

> Ble 이용하여 주변 블루투스 기기 검색 내역 저장

```kotlin
// 정보 저장 시점을 기준으로 한 unix 시간
override var time: Long?

// 기기 부팅 기준, 이벤트가 측정된 timeStampNanos
val timeStampNanos: Long?

// 검색한 블루투스 기기의 고유한 주소
val MAC: String?

// 검색한 블루투스 기기의 타입. CLASSIC, BLE, DUAL, UNKNOWN 중 하나. 자세한 정보는 BluetoothDevice 문서 참고
val type: Int?

// 검색한 블루투스 기기의 종류. 자세한 정보는 BluetoothClass.Device 문서 참고
val class_id: Int?

// dBm 기준 신호 강도
val level: Int?

// 검색한 블루투스 기기의 이름
val name: String?
```



### BluetoothModel

> 블루투스 이용하여 블루투스 기기 검색 내역 저장

```kotlin
// 정보 저장 시점을 기준으로 한 unix 시간
override var time: Long?

// 검색한 블루투스 기기의 고유한 주소
val MAC: String?

// 검색한 블루투스 기기의 타입. CLASSIC, BLE, DUAL, UNKNOWN 중 하나. 자세한 정보는 BluetoothDevice 문서 참고
val type: Int?

// 검색한 블루투스 기기의 종류. 자세한 정보는 BluetoothClass.Device 문서 참고
val class_id: Int?

// dBm 기준 신호 강도
val level: Int?

// 검색한 블루투스 기기의 이름
val name: String?
```



### CallModel

> 전화 수신, 수신 부재중, 발신  정보 저장

```kotlin
// 정보 저장 시점을 기준으로 한 unix 시간
override var time: Long?

// 수,발신의 번호
val phnNumber: String?

// 수신, 발신, 수신 부재중의 CallLog 타입. 구체적인 constants는 Android docs의 Calllog.Calls.Type 참고
val type: String?

// 수신, 발신 시점을 기준으로 한 unix 시간
val date: String?

// 초 기준 수, 발신의 기간
val duration: String?
```



### InstalledAppModel

> 설치된 앱 정보 저장

```kotlin
// 정보 저장 시점을 기준으로 한 unix 시간
override var time: Long?

// 패키지 명
val pkgName: String?
```



### LightModel

> light sensor를 이용, light 정보 저장

```kotlin
// 정보 저장 시점을 기준으로 한 unix 시간
override var time: Long?

// 이벤트가 수신된 time in nano seconds
val elapsedTime: Long?

// 수집한 센서의 hardware 이름.
val sensorName: String

// 센서 수집 정확도
val accuracy: Int?

// 센서 측정 값
val value: Float
```



### LocationDeviceModel

> 위치 정보를 저장

```kotlin
// 정보 저장 시점을 기준으로 한 unix 시간
override var time: Long?

// 위도
val latitude: Double,

// 경도 
val longitude: Double,

// 고도
val altitude: Double
```



### NotificationModel

> 상태바 알림 정보 저장

```kotlin
// 유저의 알림바 사용 상태 저장. POST(새로 알림이 생김) 또는 REMOVED(유저가 알림을 지움).    
val type: Int

// 알림이 온 시점의 unix 시간
    override var time: Long?

// unique instance key for sbn
    val key: String

// 알림의 앱 패키지 명
    val pkgName: String

// type이 REMOVED일 경우, 삭제된 이유. 구체적인 constants는 NotificationListenerService의 REASON_??? 을 참조
    val cancelReason: Int?
```



### NotificationRankingModel

> 상태바 랭킹 정보를 저장합니다. 이때 랭킹이란 상태바 내에서의 알림 순서이며, 새로운 알림이 생성될 때나 알림이 지워질 때 변동됩니다. 

```kotlin
// 정보 저장 시점을 기준으로 한 unix 시간
override var time: Long?

// Ranking 정보를 갖고 있는 리스트
var rankingList: ArrayList<Ranking> = ArrayList()
```

 

**Ranking**

```kotlin
// unique instance key for sbn
val key: String

// 상태바 내에서 알림의 순서
val rank: Int

// API 28이상에서 동작. 알림에 대한 유저의 선호도. 구체적인 constants는 NotificationListenerService의 getUserSentiment() 참조.
val userSentiment: Int?
```



### PackageModel

> 설치, 업데이트, 삭제된 앱 정보 저장

```kotlin
// 정보 저장 시점을 기준으로 한 unix 시간
override var time: Long?

// 패키지명
val pkgName: String?

// PackageModel 내에 정의된 type. DEFAULT, INSTALL, UPDATE, UNINSTALL 중 하나
val type: Int

// broadcast 에 잡힌 action. PACKAGE_ADDED, PACKAGE_FIRST_LAUNCH, PACKAGE_REMOVED, PACKAGE_FULLY_REMOVED, PACKAGE_REPLACED, PACKAGE_CHANGED 중 하나. 자세한 정보는 intent action 문서 참고
val action: String?
```



### PowerModel

> 배터리 정보 저장 (변화가 있을 때마다)

```kotlin
// 정보 저장 시점을 기준으로 한 unix 시간
override var time: Long?

// 배터리 충전시 저장되는 정보
val charged: Charged?

// 핸드폰 도킹시 저장되는 정보
val docked: Docked?
```



**Charged**

```kotlin
// BATTERY, AC, USB, WIRELESS
val plug: Int

// CHARGING, DISCHARGING, FULL, NOT_CHARGING, UNKNOWN
val batteryStatus: Int,

// 배터리 레벨. 0에서 EXTRA_SCALE까지. 
val level: Int

// 배터리 스케일. 최대 배터리 충전 수준
val scale: Int

// 배터리 잔량 표시. level * 100 / scale
val batteryPct: Float?
```



**Docked**

```kotlin
// Docking 되었는지 여부
val isDocked: Boolean

// Intent.EXTRA_DOCK_STATE_CAR
val isCar: Boolean

// Intent.EXTRA_DOCK_STATE_DESK or Intent.EXTRA_DOCK_STATE_LE_DESK or Intent.EXTRA_DOCK_STATE_HE_DESK
val isDesk: Boolean
```



### ProximityModel

> proximity sensor 이용, proximity 정보 저장

```kotlin
// 정보 저장 시점을 기준으로 한 unix 시간
override var time: Long?

// 이벤트가 수신된 time in nano seconds
val elapsedTime: Long?

// 수집한 센서의 hardware 이름.
val sensorName: String

// 센서 수집 정확도
val accuracy: Int?

// 센서 측정 값
val value: Float
```



### ScreenLockModel

> Screen Lock/Unlock 정보 저장 (변화가 있을 때마다)

```kotlin
// 정보 저장 시점을 기준으로 한 unix 시간
override var time: Long?

// Screen Lock / Unlock
val type: Int
```



### ScreenModel

> Screen On/Off 정보 저장 (변화가 있을 때마다)

```kotlin
// 정보 저장 시점을 기준으로 한 unix 시간
override var time: Long?

// Screen ON / Screen OFF
val type: Int
```



### SensorModel

> LIght, Proximity 센서 정보 저장

```kotlin
// 정보 저장 시점을 기준으로 한 unix 시간
override var time: Long?

// 수집한 센서의 hardware 이름. light 또는 proximity.
val eventName: String

// 센서 수집 정확도
val accuracy: Int?

// 센서 측정 값
val value: Float
```



### SMSModel

> 문자 수신시 정보 저장

```kotlin
// 정보 저장 시점을 기준으로 한 unix 시간
override var time: Long?

// 발신자 전화번호
val address: String?

// 수신시점
val date: String?

// 문자 읽음 여부
val read: Int?

// 메시지 프로토콜
val protocol: String?

// 문자 
val body_length: Int?
```



### UsageStatsModel

> 앱 사용 정보 저장

```kotlin
// 정보 저장 시점을 기준으로 한 unix 시간
override var time: Long?

// 앱 패키지 명
val packageName: String

// 처음 앱이 시작된 unix 시간
val firstTimeStamp: Long?

// 앱이 종료된 unix 시간
val lastTimeStamp: Long?

// 포그라운드에서 사용되었던 총 시간
val totalTimeForeground: Long

// API 29 이상에서 동작. 앱이 UI에서 보였던 총 시간
val totalTimeVisible: Long?
```



## Tracker

위치: trackers/

**부모 클래스/인터페이스**

* [TrackerInterface](#TrackerInterface)
* [Tracker](#Tracker)

**5분마다 수집**

* [AppUsageTracker](#AppUsageTracker)
* ~~[BluetoothTracker](#BluetoothTracker)~~
* [NotificationRankingTracker](#NotificationRankingTracker)

**Broadcast로 수집**

* [ActivityRecognitionTracker](#ActivityRecognitionTracker)
* [CallTracker](#CallTracker)
* [InstalledAppTracker](#InstalledAppTracker)
* [LightTracker](#LightTracker)
* [NotificationListener](#NotificationListener)
* [PackageTracker](#PackageTracker)
* [PowerTracker](#PowerTracker)
* [ProximityTracker](#ProximityTracker)
* [ScreenTracker](#ScreenTracker)
* [SMSTracker](#SMSTracker)

**이외**

* [LocationTracker](#LocationTracker)
* ~~[BleTracker](#BleTracker)~~



### TrackerInterface

> Tracker 모듈이 상속하는 인터페이스. start(), stop() 을 정의함.



### Tracker

> 모든 tracker가 상속하는 부모 클래스. TrackerInterface를 상속받음. 



### ActivityRecognitionTracker

> ActivityRecognitionModel 정보 저장 위해 사용자 활동 정보를 수집하는 트래커

* activity와 transition의 조합이 바뀔 때마다 실행됨
* activity는 다음 6 가지가 존재함

 ```kotlin
DetectedActivity.IN_VEHICLE, DetectedActivity.ON_BICYCLE, DetectedActivity.ON_FOOT,
DetectedActivity.RUNNING, DetectedActivity.STILL,
DetectedActivity.WALKING
 ```

* transition은 다음 2 가지가 존재함

 ```kotlin
ActivityTransition.ACTIVITY_TRANSITION_ENTER, ActivityTransition.ACTIVITY_TRANSITION_EXIT
 ```

* ActivityRecognitionTracker는 activity 6 가지와 transition 2 가지를 조합한 12 개의 이벤트를 수신함



### AppUsageTracker

> UsageStatsModel 정보 저장 위해 앱 사용 정보를 수집하는 트래커

- 서비스가 시작할 때 한 번 실행 후, 20 시간마다 실행됨
- 서비스 시작시 실행은 1개월 전 ~ 실행 시점 기간의 앱 정보를 수집함
- 이후 20시간 간격의 실행은 1일 전 ~ 실행 시점 기간의 정보를 수집함

* UsageStatsModel 내부에서 중복 정보를 제외하고 저장함. 자세한 정보는 관련 코드 참고.



### BleTracker

> BleModel 정보 저장 위해 블루투스 정보를 수집하는 트래커

* 현재는 사용하지 않음
* 10초동안 주변 블루투스 기기를 검색함



### BluetoothTracker

> BluetoothModel 정보 저장 위해 블루투스 정보를 수집하는 트래커

* 5분마다 실행됨
* 12초 가량(안드로이드에서 정의) 주변 블루투스 기기를 검색함
* BluetoothModel 내부에서 MAC 기준, 1분 내 중복 정보를 제외하고 저장함



### CallTracker

> CallModel 정보 저장 위해 전화 수신, 수신 부재중, 발신 정보를 수집하는 트래커

- Broadcast 등록
- 전화 수신, 발신 때마다 동작 
- 수신, 수신 부재중, 발신의 세 가지 상태 구분 가능

* CallModel 내부에서 date, phnNmber 기준 중복 정보를 제외하고 저장함



### InstalledAppTracker

> InstalledAppModel 정보 저장 위해 설치된 앱 정보를 수집하는 트래커

* Broadcast 등록
* 서비스 최소 실행 시 1번 동작



### LightTracker

> LightModel 정보 저장 위해 light 정보를 수집하는 트래커

* SensorListener 등록
* 센서 정보가 바뀔 때마다(onSensorChanged) 동작
* LightModel 내부에서 5분에 한 번씩만 정보 저장되도록 조절



### LocationTracker

> LocationDeviceModel 정보 저장 위해 위치 정보를 수집하는 트래커

* GoogleAPI FusedLocationProviderClient 이용
* tracking Interval=5분, fastestInterval=1분



### NotificationListener

> NotificationModel 정보 저장 위해 알림 정보를 수집하는 트래커

* Broadcast 등록
* listner가 처음 연결되었을 때, 상태바에 알림이 새로 생겼을 때, 사용자가 알림을 지웠을 때 의 세 경우에 동작



### NotificationRankingTracker

> NotificationRankingModel 정보 저장 위해 상태바의 알림 순서 정보를 수집하는 트래커

* 5분마다 실행됨



### PackageTracker

> PackageModel 정보 저장 위해 설치, 업데이트, 삭제된 앱 정보를 수집하는 트래커

* Broadcast 등록

  ``` kotlin
  // Action app installed
  Intent.ACTION_PACKAGE_ADDED
  Intent.ACTION_PACKAGE_FIRST_LAUNCH
  // Action app uninstalled
  Intent.ACTION_PACKAGE_REMOVED
  Intent.ACTION_PACKAGE_FULLY_REMOVED
  // Action app replaced
  Intent.ACTION_PACKAGE_REPLACED
  Intent.ACTION_PACKAGE_CHANGED
  ```

  

### PowerTracker

> PowerModel 정보 저장 위해 배터리 정보를 수집하는 트래커

* Broadcast 등록
* 배터리 상태가 바뀌었을 때(배터리 충전 시작, 배터리 충전 종료), 독 상태가 변경되었을 때(독이 새로 연결되었을 때, 독 연결이 해제되었을 때) 동작



### ProximityTracker

> ProximityModel 정보 저장 위해 proximity 정보를 수집하는 트래커

* SensorListener 등록
* 센서 정보가 바뀔 때마다(onSensorChanged) 동작



### ScreenTracker

> ScreenModel 정보 저장 위해 screen on/off 정보를 수집하는 트래커 & ScreenLockModel 정보 저장 위해 screen lock/unlock 정보를 수집하는 트래커

* Broadcast 등록
* 화면이 꺼졌을 때(Screen OFF), 화면이 켜졌을 때(Screen ON) 동작
* 핸드폰 잠금이 풀렸을 때(Screen Unlock, ACTION_USER_PRESENT), 핸드폰 잠겼을 때(Screen OFF) 동작



### SMSTracker

> SMSModel 정보 저장 위해 수신한 문자 정보를 수집하는 트래커

* Broadcast 등록
* 문자가 새로 수신될 때마다 동작



## Others

* [ApiManager](#ApiManager)
* [BootBroadcastReceiver](#BootBroadcastReceiver)
* [FileSaveWorker](#FileSaveWorker)
* [MainActivity](#MainActivity)
* [TrackerService](#TrackerService)
* [UploadWholeWorker](#UploadWholeWorker)
* [Utils](#Utils)



### ApiManager

> Firestore 이용을 위해 사용하는 싱글톤 firestore api manager

* ```kotlin
  fun uploadDataInRealtime(listAdapter: ModelAdapterInterface<out ModelInterface>)
  ```

  * ModelListAdapter의 각 정보를 받아 firebase realtime database 에 저장
  * UploadWorker통해서 실행됨
  * 만약 업로드에 실패할 경우, 업로드가 성공할 때까지 30분 간격으로 재시도함
  
  

* ```kotlin
  @MainThread
  fun deleteDataInRealtime(context: Context)
  ```

  * DEBUG모드에서만 동작함.
  * realtime database를 삭제하는 모듈. **함부로 쓰면 안됨**

### TrackerService

> trackers/ 하위의 모든 모듈의 동작을 제어

* 5분마다 또는 Broadcast에 따라 tracker 동작 제어
* 1시간에 한번씩 listAdapter에 저장된 정보를 firestore에 보냄
* 서비스가 종료될 때 listAdapter에 저장된 정보를 firestore에 보냄
* 현재는 사용하지 않으나, csv 형식으로 앱 하위 files에 저장하는 함수도 구현되어 있음



### Utils

> 공용 유틸 싱글톤 모듈



###  MainActivity

> UI 담당 및 permission 체크 담당. TrackerService 모듈 제어. UploadWorker 제어



### UploadWorker

> 하루 간격으로 1. 배터리 수준이 낮지 않고 2. 와이파이가 연결되어 있을 때 firebase realtime database에 데이터 업로드



## Permissions

```xml
// LoctationTracker
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
// Background Location 
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />

// TrackerService의 백그라운드 실행
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.INTERNET"/>

// Bluetooth
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>

// CallTracker
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<uses-permission android:name="android.permission.READ_CALL_LOG"/>

// AppUsageTracker
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"
    tools:ignore="ProtectedPermissions"/>

// SMSTracker
<uses-permission android:name="android.permission.RECEIVE_SMS"/>
<uses-permission android:name="android.permission.READ_SMS"/>
```

## Library

```
// 서버 통신, retrofit library
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

//   location sensor, google API
implementation "com.google.android.gms:play-services-location:17.0.0"

//    Notification
implementation 'androidx.core:core:1.3.0'

// csv 파일 저장
implementation 'com.opencsv:opencsv:5.2'

// firebase analytics, crashlytics
implementation 'com.google.firebase:firebase-analytics:17.4.4'
implementation 'com.google.firebase:firebase-analytics-ktx:17.4.4'
implementation 'com.google.firebase:firebase-crashlytics:17.1.1'

// firestore
implementation 'com.google.firebase:firebase-firestore-ktx:21.5.0'

//    Json Serializer
implementation 'com.google.code.gson:gson:2.8.6'

//    WorkManager
implementation "androidx.work:work-runtime-ktx:2.3.4"

//    Realtime database
implementation 'com.google.firebase:firebase-database-ktx:19.3.1'
```