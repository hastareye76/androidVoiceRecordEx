package com.altimedia.audiorecoderexample

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.IOException
import java.lang.Thread.sleep
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


private var rectag: String = "VOICEREC"

class VoiceSender(context: MainActivity, val ip: String, val port: Int) : Runnable {

    //코틀린 클래스는 주 생성자(primary constructor) 부 생성자(secondary constructor) 를 제공하고 있다.
    /*
    보통 주 생성자는 클래스를 초기화할 때 주로 사용하는 간략한 생성자로 클래스 본문 밖에서 정의 부 생성자는 클래스
    본문안에서 정의 합니다. 또한 코틀린의 초기화 블럭 (initalizer block)을 통해서 초기화 로직을 추가 할 수 있다.

    주 생성자
    :클래스 이름 뒤에 오는 괄호로 둘러싸인 코드를 주 생성자라 부른다. VoiceSender는 주 생성자 형식으로 선언하였다.
    init {} 블럭에서 초기화 할 수 있다.

    부 생성자
    :클래스 본문안에 constructor 라는 키워드를 통해서 정의 된 생성자를 부 생성자라고 한다.
    // 1번. super()를 통해 상위 클래스의 생성자를 호출함.
        class MyButton : View {
            constructor(ctx: Context)
                : super(ctx){
             }
         }
     */

    private var socket = DatagramSocket()
    private val bufferSize: Int = 1024
    val buffer = ByteArray(bufferSize)
    val context: Context = context
//실제 기기에서 테스트시에는 해당 내장 메모리에 해당 파일이 존재하지 않아서 crash가 발생한다.
//    val fileName = "/example.txt"
//    val filepath = context.filesDir.absolutePath + fileName
//    var file = File(filepath)
//    val fileLength = file.length()
    val address = InetAddress.getByName(ip)

//    val inputStream = file.inputStream()
    var bytesRead: Int = 0
    var totalBytesRead: Long = 0

    //녹음 버튼 레코딩 변수
    var isRcording : Boolean = false

//init block을 통해서 초기화 할 수 있다.
//    init {
//        try {
//            val fileContent = readFromFile(context, fileName)
//            Log.d(mytag, "fileread success [$fileContent]")
//        }catch (e: Exception){
//            e.printStackTrace()
//        }
//
//
//    }

    //AudioRecoder
    //오디오 표준 sample rate 44.1 khz [1초당 44100번 샘플링하여 기록 하였다는 의미]
    private var mSampleRate: Int = 44100
    private var mAudioChannelCount = AudioFormat.CHANNEL_IN_MONO
    private var mAudioFormat = AudioFormat.ENCODING_PCM_16BIT

    //어떤 하드웨어 장비로 부터 획득 할 것인가
    private var mAudioSource: Int = MediaRecorder.AudioSource.MIC
    private var mATrackBufferSize: Int

    var mAudioRecord: AudioRecord? = null
    lateinit var data: ByteArray

    var main: MainActivity? = null


    init {
        //녹음용 버퍼에 사용할 최소 버퍼를  AudioRecoder에 물어봐서 가져온다.
        mATrackBufferSize =
            AudioRecord.getMinBufferSize(mSampleRate, mAudioChannelCount, mAudioFormat);
        Log.d(rectag, "AudioTrack 의 최소 버퍼 사이즈 =[$mATrackBufferSize]")
        data = ByteArray(mATrackBufferSize)

        Log.d(rectag, "Audio")
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(rectag, "VoiceSener 에서 RECORD_AUDIO 권한 체크 ")
        }
        Log.d(rectag, "VoiceSener 에서 RECORD_AUDIO 권한 있음 AudioRecord 객체 생성 ")
        mAudioRecord =
            AudioRecord(mAudioSource, mSampleRate, mAudioChannelCount, mAudioFormat, mATrackBufferSize)

        main = context;

    }


//파일 read test
//    fun readFromFile(context: Context, fileName: String): String {
//        val fileInputStream = context.openFileInput(fileName)
//        val inputStreamReader = InputStreamReader(fileInputStream)
//        val bufferedReader = BufferedReader(inputStreamReader)
//
//        val stringBuilder = StringBuilder()
//        var line: String? = bufferedReader.readLine()
//
//        while (line != null) {
//            stringBuilder.append(line)
//            line = bufferedReader.readLine()
//        }
//
//        bufferedReader.close()
//        inputStreamReader.close()
//        fileInputStream.close()
//
//        return stringBuilder.toString()
//    }

    fun startRecording() {
        Log.d(rectag, "startRecording voice recodeing start")
        mAudioRecord?.startRecording()

    }

    fun stopRecording() {
        Log.d(rectag, "stopRecording voice recodeing stop")
        mAudioRecord?.stop()
    }

    public fun getRecordStatus() : Boolean{
        Log.d(rectag, "getRecordStaus call isRcording =[$isRcording] ")
        return isRcording


    }

    public fun setRecordStatus(status: Boolean){
        Log.d(rectag, "setRecordStaus call isRcording =[$status] ")
        isRcording = status
    }




//        val outputFilePath = "$externalStoragePath/path_to_output_file.wav"
//        var outputFile = File(outputFilePath)
//        if(!outputFile.exists()){
//            //outputFile.createNewFile()
//            Log.d("MainActivity", "startRecording no file")
//        }else{
//            Log.d("MainActivity", "startRecording  file")
//        }
//        Log.d("MainActivity", "startRecording outputFile path="+outputFilePath )
        //val outputStream = DataOutputStream(FileOutputStream(outputFilePath))
//
//        //권한이 체크
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.RECORD_AUDIO
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.d("MainActivity", "startRecording call RECORD_AUDIO permission err" )
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        } else {
//            Log.d("MainActivity", "startRecording call RECORD_AUDIO permission ok" )
//        }
//        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize)
//
//        audioRecord.startRecording()
//
//        Thread {
//            Log.d("MainActivity", "스레드 ........"+outputFile )
//            while (true) {
//                val bytesRead = audioRecord.read(data, 0, bufferSize)
//                outputStream.write(data, 0, bytesRead)
//            }
//        }.start()
//    }

    override fun run() {
        Log.d(rectag, "Thread run call")
        //녹화 중이면 데이타를 가져와서 서버로 전송을 해줘야 한다.
        while (isRcording) {

            val ret: Int = mAudioRecord?.read(data, 0, mATrackBufferSize) ?: -1
            //  AudioRecord의 read 함수를 통해 pcm data 를 읽어옴
            if(ret == -1){
                Log.d(rectag, "voice no data")
                sleep(100)
                continue
            }else {
                //Log.d(rectag, "recoding data read bytes is $ret")
                //자 획득한 데이타를 이제 서버로 보내 줘야 한다.
                val packet = DatagramPacket(data, ret, address, port)

                try {
                    socket.send(packet)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            totalBytesRead += bytesRead.toLong()
            //Log.d(rectag, "data send [$ret] total [$totalBytesRead]")
        }


    }







//        while (totalBytesRead < fileLength) {
//            bytesRead = inputStream.read(buffer)
//            if (bytesRead == -1) {
//                break
//            }
//
//            val packet = DatagramPacket(buffer, bytesRead, address, port)
//            Log.d(mytag, "data send")
//            socket.send(packet)
//
//            totalBytesRead += bytesRead.toLong()
//        }
//
//        socket.close()
//        inputStream.close()


}

