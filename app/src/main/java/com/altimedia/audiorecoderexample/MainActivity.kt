package com.altimedia.audiorecoderexample

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.altimedia.audiorecoderexample.databinding.ActivityMainBinding
import java.io.DataOutputStream
import java.io.FileOutputStream

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

    //AudioRecoder
    //오디오 표준 sample rate 44.1 khz [1초당 44100번 샘플링하여 기록 하였다는 의미]
    private val SAMPLE_RATE = 44100
    private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

    val externalStoragePath = Environment.getExternalStorageDirectory().absolutePath





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

        //ActionBar를 사용한다. toolbar
        setSupportActionBar(binding.appBarMain.toolbar)

        //바인딩 된 이메일 아이콘을 눌렀을 경우 .... 이걸 녹화 버튼으로 사용한다.
        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "녹화 시작 버튼을 누렀다.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            startRecording()
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

    fun startRecording() {
        Log.d("MainActivity", "startRecording call")
        //우리가 지정한 포맷으로 녹음을 진행할때 필요한 버퍼를 시스템에 요구해서 받아낸다.(
        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        //시스템이 최소한의 필요한 버퍼 정보를 주었다. 이를 기반으로 사용할 버퍼를 정의 한다.
        val data = ByteArray(bufferSize)
        //자이제 AudioRecord 객체를 생성한다.
        val audioRecord : AudioRecord

        val outputFile = "$externalStoragePath/path_to_output_file.wav"
        val outputStream = DataOutputStream(FileOutputStream(outputFile))

        //권한이 체크
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MainActivity", "startRecording call RECORD_AUDIO permission err" )
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        } else {
            Log.d("MainActivity", "startRecording call RECORD_AUDIO permission ok" )
        }
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize)

        audioRecord.startRecording()

        Thread {
            while (true) {
                val bytesRead = audioRecord.read(data, 0, bufferSize)
                outputStream.write(data, 0, bytesRead)
            }
        }.start()
    }
}