package com.altimedia.audiorecoderexample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.altimedia.audiorecoderexample.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.lang.StringBuilder

//https://www.flaticon.com/search?word=mic
class MainActivity : AppCompatActivity() {
    /*
       코틀린은 기본적으로 null 사용을 지양하고 있다.
       목적에 맞게 늦은 초기화 기법이 존재한다.
       lateinit 과 lazy 이다.
       현재
       appBarConfiguration binding 는 모두 var 로 선언되어져 있다. 변경될 수 있기 때문이다. 늦은
       초기화를 선언했다면 변경이 가능한 변수 이므로 val을 사용해서는 안된다.
       이를 lateinit 으로 즉 늦은 초기화를 하겠다고 명시적으로 선언하였다.

       lateinit 으로 선언해 부고 초기화를 하지 않으면 컴파일 단계에서 에러가 발생한다.
       lateinit 의 경우에는 계속하여 값이 변경될 수 있다는 속성을 위해 무조건 var 을 사용해야 하며,
       Primitive Type (Int, Float, Double, Long 등) 에는 사용할 수 없다.

       by lazy
       fun main() {
            lateinit var text: String
            val textLength: Int by lazy {
                text.length
            }

            // 대충 중간에 뭔가 했음
            text = "H43RO_Velog"
            println(textLength)
        }
        살펴보면 by lazy 구문을 통해서 어떤 생성자를 넣어준 모양이다. 값을 가져와야 할 변수가 lateinit
        으로 선언되어 있어서 아직 text가 초기화 되지 않는 text의 속성을 가져와야 한다는 점이다.

        by lazy 는 선언되는 시점에는 초기화를 할 방법이 없지만 이후 의존되는 값 (여기서는 text가 될것 인데 )
        들이 초기화 된 이후에 값을 채워 놓고 싶을때 사용한다. 즉 호출시 에는 어떻게 최기화를 해줄지 에 대한 정의
        를 해둔다고 보면 된다.

        val로 선언되어져 있음을 볼 수 있는데 이는 단한번 늦은 초기화가 이루어지고 이후에는 값이 불변함을 보장
        한다는 의미다.

        사용 Tip
        안드로이드에선 이전 액티비티에서 넘어온 Intent Bundle Extra 등을 현재 액티비티 멤버 변수에 by lazy 로 받아와,
        선언해두고 사용 시에 intent.extra 등으로 번들을 뜯어볼 수 있다.
        이렇게 하면 생명주기를 위반하지 않고 안전하게 클래스 전역에서 사용할 수 있는 값을 갖고올 수 있다.
     */

    //늦은 초기화 변수 선언
    //App의 App Bar관련된 동작을 정의하는데 사용되는 클래스이다. 주로 Toolbar 와 결합하여 App bar를 구성한다.
    private lateinit var appBarConfiguration: AppBarConfiguration

    //DataBinding
    /*
    Activatiy 나 Fragment 의 데이타를 화면에 출력하는 부분을 도와 주는 AAC(Android Architecture Component)
    [테스트와 유지관리가 쉬운 앱을 디자인하도록 도움을 주는 라이브러리 모음]
    https://4z7l.github.io/2020/09/21/android-aac.html
    https://academy.realm.io/kr/posts/android-architecture-component-on-kotlin/
    추후에 정독해 보자 지금은 내용이 너무 깊다.

    view binding
    기본적으로 layout을 정의한 xml 파일내에 UI 컴포넌트를 접근하기 위해서 findViewById 메소드를 사용하지만
    코틀린에서 도입된 코틀린 익스텐션을 통해서 쉽게 view를 다루 있었다.
    그러나 이제는 view bind 이 이 코틀린 익스텐션을 대체한다.

    view bind 은 코틀린 환경의 안드로이드에서 뷰에 접근하는 새로운 방식이다.
    1.layout xml 파일 생성
    2. bind 초기화
    3. root view 표시
    4. ID로 뷰에 접근
    코드로 보면 다음과 같다.
    class MainActivity : AppCompatActivity() {

    // 여기서 lazy로 초기화하고 사용해도 됨
    // val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 바인딩 초기화
        val binding = ActivityMainBinding.inflate(layoutInflater);

        // 2. 레이아웃(root뷰) 표시
        setContentView(binding.root);

        // 3-1.뷰바인딩으로 버튼 접근
        binding.myButton.setOnClickListener {

            // 3-2. 뷰바인딩으로 텍스트뷰 접근
            binding.myTextView.text = "바인딩이 잘 되네요!";
        }
     */
    private lateinit var binding: ActivityMainBinding


    private val mytag: String = "VOICEREC"

    //접속할 서버에 대한 정보[내빌드 PC에서 실행되고 있다.
    private var SERVER_IP: String = "10.1.1.166"
    private var SERVER_PORT: Int = 29001
    private var SERVER_RECV_PORT: Int = 19012

    //어플의 설정을 저장할 json 파일선언
    private var LANGUAGE: String = "kor"
    private val saveSessionDataFile: String = "setting.json"

    //외장메모리 path
    val externalStoragePath: String = Environment.getExternalStorageDirectory().absolutePath

    //multi permission 권한
    private val multiplePermissionCode = 100

    //var array1 = arrayOf(1,2,4) : 특정값을 넣어서 배열을 생성할때 사요한다.
    //var array2 = Array(10, {0}) : 크기만 정해서 배열을 생성할때 사용한다.
    private val requiredPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    lateinit var context: Context

    lateinit var _voiceSender: VoiceSender
    lateinit var _recvThread: VoiceRecver
    var isStartRecv: Boolean = false

    //헤드셋 plug in/out을 받기 위해서
    private lateinit var receiver: MyBroadcastReceiver

    lateinit var fabButton: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //1.layout xml 파일 생성 AndroidManifest 파일에 보면 MainActivity 연결된 layout은
        //activity_main.xml 파일이다. view bind 옵션이 build.gradle 에서 vidwbinding 옵션이 true
        //로 설정되면 activity_main => ActivityMain + Binding 으로 명청이 달라진다.
        //아래 ActivityMainBinding 는 activity_main.xml 와 동일한다.
        //자동 생성된다. 이제 자동 생성된 view binding 클래스에서 제공되는 inflate 라는 메소드를
        //호출하여 MainActivity에서 사용할 binding 클래스의 인스턴스를 생성한다.
        binding = ActivityMainBinding.inflate(layoutInflater)
        //root view 표시
        setContentView(binding.root)

        fabButton = findViewById(R.id.fab)

        //ActionBar를 사용한다. toolbar
        setSupportActionBar(binding.appBarMain.toolbar)

        Log.d(mytag, "startSocketRecording call start VoiceSender Create ")
        _voiceSender = VoiceSender(this, SERVER_IP, SERVER_PORT)
        _recvThread = VoiceRecver(this, SERVER_IP, SERVER_RECV_PORT)

        //바인딩 된 이메일 아이콘을 눌렀을 경우 .... 이걸 녹화 버튼으로 사용한다.
        binding.appBarMain.fab.setOnClickListener { view ->
            if (!_voiceSender.getRecordStatus()) {
                Snackbar.make(view, "녹음 시작!!!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                startSocketRecording()
                _voiceSender.setRecordStatus(true)
                //이때는 녹음 시작이라서 버튼은 정지 버튼을 보여준다.
                setImageByRecState(fabButton, _voiceSender.getRecordStatus())
            } else {
                Snackbar.make(view, "녹음 정지!!!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                stopRecording()
                _voiceSender.setRecordStatus(false)
                //이때는 녹음 정지라서 버튼은 녹음 버튼을 보여준다.
                setImageByRecState(fabButton, _voiceSender.getRecordStatus())
            }


        }
        //
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        //app:defaultNavHost="true" 로 지정된 fragment를 NavHost로 지정해 두었다.
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        //네비게이션 화면은 AppBarConfiguration 객체를 사용해서 App 의 상단 왼쪽 toolbar의 탐색버튼(일명 삼발이) 관리한다.
        //여기에서는 3가지 탐색버튼이 존재한다. resource 파일에 정의되어져 있다. 탐색버튼은 DrawLayout으로 지정된 navigation
        //Drawer가 열렸을때 보여질때(이말을 탐색 그래프의 최상위에 있다고 표현을 하는 것으로 보인다.) 그외 상태에서는 보여진다.

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            //코틀린에서 setOf 함수는 고유한 원소로 구성된 변경 불가능한(immutable) 집합(set)을 생성하는데 사용하는 함수이다.
            //이 함수는 주어진 인자들을 기반으로 한 집합을 생성하며, 중복된 원소는 제거한다.
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )

        //액션바에 네이게이션 뷰를 붙인다.
        setupActionBarWithNavController(navController, appBarConfiguration)
        //navigation view 에 Host를 연결해 준다.
        navView.setupWithNavController(navController)


//      requestExtWritePermission()
        Log.d(mytag, "onCreate multi permission request start")
        requestPermissions()
        Log.d(mytag, "onCreate multi permission request end & onCreate complete")
//
//        context = this

        //헤드셋의 plug in/out 이벤트를 받기 위해서
        receiver = MyBroadcastReceiver(this)
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_HEADSET_PLUG)
        }

        //등록을 해줘야  동작한다.
        registerReceiver(receiver, filter)

        //우리는 데이타
        readSessionData(saveSessionDataFile)

    }

    //접속할 서버에 대한 정보[내빌드 PC에서 실행되고 있다.
//    private val SERVER_IP : String = "10.1.1.166"
//    private val SERVER_PORT : Int = 29001
//    private val SERVER_RECV_PORT : Int = 19012
    fun readSessionData(fileName: String) {
        val path = externalStoragePath + "/" + fileName
        val file = File(path)

        val fileWriter: FileWriter

        val bufferedReader: BufferedReader
        if (!file.exists()) {

            Log.d(mytag, "저장된 파일이 없다.!!!!")
            file.createNewFile()
            fileWriter = FileWriter(file)

            //JSON 객체 생성하여 ( key, value ) 방식으로 작성하여 넣는다.
            val jsonObj = JSONObject()
            jsonObj.put("selectedLang", LANGUAGE)
            jsonObj.put("serverAddress", SERVER_IP)

            fileWriter.write(jsonObj.toString())
            fileWriter.flush()
            fileWriter.close()
        } else {
            Log.d(mytag, "저장된 파일이 있다..")
//            file.delete()
            try {
                bufferedReader = BufferedReader(file.reader())
                var strBuilder = StringBuilder()
                var jsonString: String?

                bufferedReader.useLines { lines ->
                    lines.forEach {
                        strBuilder.append(it)
                    }
                }
                bufferedReader.close()

                jsonString = strBuilder.toString()
                if (jsonString != null) {
                    val jsonObj = JSONObject(jsonString)
                    val selectedLang = jsonObj.getString("selectedLang")
                    val serverAddress = jsonObj.getString("serverAddress")

                    Log.d(mytag, "selectedLang[$selectedLang], serverAddress[$serverAddress]")
                    SERVER_IP = serverAddress
                    LANGUAGE = selectedLang
                    //세션 데이타가 저장되어있다면....getJSONArray 함수는 null 대신 exception 을 던진다.
                    var dataArray = jsonObj.getJSONArray("data")

                    for (i in 0 until dataArray.length()) {
                        val sessionObject = dataArray.getJSONObject(i)
                        val title = sessionObject.getString("title")
                        val date = sessionObject.getString("date")
                        val elapsed = sessionObject.getInt("elapsed")
                        Log.d(mytag, "title[$title], data[$date], elapsed[$elapsed]")
                    }
                }
            } catch (e: Exception) {
                Log.d(mytag, e.stackTraceToString())
            }


        }

    }


    fun setImageByRecStateEx(state: Boolean) {
        Log.d(mytag, "setImageByRecStateEx call 녹음 상태 [$state]")
        if (state == true) {
            fabButton.setImageResource(R.drawable.ic_stop)
        } else {
            fabButton.setImageResource(R.drawable.baseline_record_voice_over_24)
        }

    }

    fun setImageByRecState(fabButton: FloatingActionButton, state: Boolean) {
        Log.d(mytag, "setImageByRecState call 녹음 상태 [$state]")
        if (state == true) {
            fabButton.setImageResource(R.drawable.ic_stop)
        } else {
            fabButton.setImageResource(R.drawable.baseline_record_voice_over_24)
        }
    }

    //송/수신 스레드.
    var thread: Thread? = null
    var recverThread: Thread? = null
    fun startSocketRecording() {
        //스레드를 만들어서 접속한다.
        Log.d(
            mytag,
            "startSocketRecording call start VoiceSender Create end $SERVER_IP:$SERVER_PORT"
        )
        _voiceSender.startRecording()
        thread = Thread(_voiceSender)
        Log.d(mytag, "recording and packet send thread start")
        thread!!.start();

        //수신부 스레드를 시작해 준다.
        if (!isStartRecv) {
            isStartRecv = true
            recverThread = Thread(_recvThread)
            Log.d(mytag, "from server data packet receive thread start")
            recverThread!!.start();
        }
    }

    fun stopRecording() {
        Log.d(mytag, "stopRecording call stop VoiceSender")
        _voiceSender.stopRecording();
    }


    //다중권한
    private fun requestPermissions() {
        //요청한 권한에 대해서 거절되거나 아직 수락하지 않은 권한을 저장할 문자열 배열 리스트
        var rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 체크하여 현재 권한을 받았는지 체크 for in 함수를 통해서 배열에서 하나씩
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //만약 권한이 없다면 reject 된 권한을 list에 추가를 해준다.
                rejectedPermissionList.add(permission)
            }
        }
        //만약 거절된 권한이 있다면 사용자가에게 재 요청을 하여 권한을 받아와야 한다.
        if (rejectedPermissionList.isNotEmpty()) {
            //권한을 요청해야 한다. arrayofNulls 는 지정된 크기의 null로 채워진 배열을 만드는 함수이다.
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            //List에 추가한것을 배열로 변경하여 함수 인자로 넘겨준다. 이번 request 요청에 대한 응답인지를 구분하기 위해서 code 값을 넣어준다. onRequestPermissionsResult 함수를 시스템에서 호출할
            //때 이 코드값을 넘겨준다.
            ActivityCompat.requestPermissions(
                this,
                rejectedPermissionList.toArray(array),
                multiplePermissionCode
            )
        }
    }

    //요청된 권한에 대한 처리를 안드로이드에서 호출해주는 콜백함수를 override 하여 처리를 해줘야 한다.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //switch 문과 비슷한 when 구문을 사용하여 처리한다. (우리가 권한을 여러번 요청할 수 있으므로 이 code로 식별
        when (requestCode) {
            multiplePermissionCode -> {
                //권한요청에 대한 결과가 존재하는지를 보고 비어 있지 않는다면. 자바에서 있던 것이군 collection을 사용하여 for문을 실행할때 인덱스와 값을 가져
                //오려면 withIndex가 구문을 사용한다.
                if (grantResults.isNotEmpty()) {
                    for ((i, permission) in permissions.withIndex()) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.d(mytag, "The user has denied to $permission")
                        } else {
                            Log.d(mytag, "The user has gain to $permission")
                        }
                    }

                }
            }

        }
    }

    private fun showExplanation(
        title: String,
        message: String,
        permission: String,
        permissionRequestCode: Int
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, id -> requestPermission(permission, permissionRequestCode) }
        builder.create().show()
    }

    private fun requestPermission(permissionName: String, permissionRequestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permissionName), permissionRequestCode)
    }

    //ActionBar에 메뉴를 생성해 넣는다.
    //AppCompatActivity.java 가 코틀린 클래스로 자동 변경되면서 setter()/getter() 있는 함수는
    //var(읽기/쓰기) getter()만 있는 함수는 val(읽기만 가능) var/val 프로퍼티의 이름은
    //get과 set 제외하고 소문자로 시작하는 것으로 변경이 되는 것으로 보인다.
    //코트린에서 클래스 의 프로퍼티를 생성하면
    /*
    프로퍼티 선언
    코틀린에서는 자바의 private String name 을 프로퍼티라고 합니다.

    class Person(
        val name: String,       //읽기 전용 val
        var isCareer: Boolean //쓸수있는 var
        )

    val 프로퍼티
    읽기 전용 프로퍼티
    private name: String 프로퍼티 생성
    public getName() : String 함수 생성
    var 프로퍼티
    쓰기 가능 프로퍼티
    private isCareer: Boolean 프로퍼티 생성
    public isCareer() 함수 생성
    public setCareer() 함수 생성
    코틀린에서 클래스만 생성하면 멤버변수와 setter/getter 가 자동으로 생성되어 아래처럼 바로 사용할 수 있습니다.
     */

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }


}