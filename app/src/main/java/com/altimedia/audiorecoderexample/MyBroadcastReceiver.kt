package com.altimedia.audiorecoderexample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

private var broadtag: String = "VOICEREC"

class MyBroadcastReceiver(context: MainActivity) : BroadcastReceiver() {
    var main: MainActivity? = null
    init {
        main = context
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(broadtag, "broadcast 테스트 !!!!")
        if (intent?.action == Intent.ACTION_HEADSET_PLUG) {
            val state = intent.getIntExtra("state", -1)
            val hasMicrophone = intent.getIntExtra("microphone", -1)

            when (state) {
                0 -> {
                    // 이어폰이 분리됨
                    if (hasMicrophone == 1) {
                        // 마이크가 있는 이어폰이었을 경우
                        // 처리할 코드를 작성하세요.
                        Log.d(broadtag, "마이크가 있는 이어폰 분리")
                        //토스트 메소드를 보여주고 현재 녹음 중이라면 녹음을 중지한다. 녹음 재 시작을 ...
                        main?.setImageByRecStateEx(false)
                        if(main?._voiceSender?.getRecordStatus() == true) {
                            Toast.makeText(context, "헤드셋 연결 해제 / 녹음을 중지합니다.", Toast.LENGTH_SHORT).show()
                            main?.stopRecording()
                            main?._voiceSender?.setRecordStatus(false)
                        }else{
                            Toast.makeText(context, "헤드셋 연결 해제 / 녹음 버튼으로 눌러 주세요", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        // 마이크가 없는 이어폰이었을 경우
                        // 처리할 코드를 작성하세요.
                        Log.d(broadtag, "마이크가 없는 이어폰 분리")
                    }
                }

                1 -> {
                    // 이어폰이 연결됨
                    if (hasMicrophone == 1) {
                        // 마이크가 있는 이어폰이었을 경우
                        // 처리할 코드를 작성하세요.
                        Log.d(broadtag, "마이크가 있는 이어폰 연결")
                        //토스트 메소드를 보여주고 현재 녹음 중이라면 녹음을 중지한다. 녹음 재 시작을 ...
                        main?.setImageByRecStateEx(false)
                        if(main?._voiceSender?.getRecordStatus() == true) {
                            Toast.makeText(context, "헤드셋 연결 / 녹음을 중지합니다.", Toast.LENGTH_SHORT)
                                .show()
                            main?.stopRecording()
                            main?._voiceSender?.setRecordStatus(false)
                        }else{
                            Toast.makeText(context, "헤드셋 연결 녹음 버튼으로 눌러 주세요", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        // 마이크가 없는 이어폰이었을 경우
                        // 처리할 코드를 작성하세요.
                        Log.d(broadtag, "마이크가 있는 이어폰 연결")
                    }
                }
            }
        }
    }
}