package com.altimedia.audiorecoderexample

import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket

private var recvtag: String = "SERVERRECV"



//수신을 담당하는 구현부임.
//서버로 부터 오는 text 데이타를 받아 드리는 부분이다.
class VoiceRecver (context: MainActivity, val ip: String, val port: Int) : Runnable {

    private val recvBufferSize = 1024

    //수신을 대기할 포트를 넣어서 생성한다.
    private var recvSocket = DatagramSocket(port)
    val receiveBuffer = ByteArray(recvBufferSize)


    init {


    }

    override fun run() {
        while (true) {
            // 서버로 부터 데이터 수신
            val receivePacket = DatagramPacket(receiveBuffer, receiveBuffer.size)
            recvSocket.receive(receivePacket)

            val serverAddr = receivePacket.address
            val serverSendPort = receivePacket.port
            val receiveData = String(receivePacket.data, 0, receivePacket.length)

            //Log.d(recvtag, "서버로 수신한 데이타 : [${receiveData}]")


        }
    }
}
