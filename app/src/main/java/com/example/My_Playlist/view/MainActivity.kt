package com.example.My_Playlist.view

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.My_Playlist.R
import com.example.My_Playlist.adapter.AudioAdapter
import com.example.My_Playlist.model.Audiotracks
import com.example.My_Playlist.service.MyPlayerService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AudioAdapter.AudioAdapterDelegate {


    var audioList: MutableList <Audiotracks> = mutableListOf(
        Audiotracks("Track1", "Relaxing", "60 cm"),
        Audiotracks("Track2", "Yoga", "60 cm"),
        Audiotracks("Track3", "Workout", "60 cm"),
        Audiotracks("Track4", "Running", "60 cm"),
        Audiotracks("Track5", "Meditation", "60 cm"),
        Audiotracks("Track6", "Studying", "60 cm"),
        Audiotracks("Track7", "Motivation", "60 cm")
        )


    private var myPlayerService: MyPlayerService? = null


    val serviceConnection = object : ServiceConnection{
        override fun onServiceDisconnected(componentName: ComponentName?) {

        }

        override fun onServiceConnected(componentName: ComponentName?, iBinder: IBinder?) {
            Log.d("TAG_X", "Connected to service")
            myPlayerService = (iBinder as MyPlayerService.MyPlayerBinder).getPlayerService()
        }
    }

    val handler = Handler()

    private val myReceiver = object : BroadcastReceiver(){
        override fun onReceive(contect: Context?, intent: Intent?) {
            intent?.getStringExtra("my_key")?.let { mediaStatus->
                handler.post {
                    media_status_textview.text = mediaStatus
                }
            }
        }
    }

    override fun audioSelect(audio: Audiotracks) {
        audioSelected_textview.text = audio.name
        myPlayerService?.playTracks(R.raw.relax)?: {
            Log.d("TAG_X", "Service started from selection")
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpRecyclerView()


        registerReceiver(myReceiver, IntentFilter("com.my.sender"))

        val intent = Intent(this, MyPlayerService::class.java)
        play_button.setOnClickListener {
            myPlayerService?.playPlayer() ?: {
                Log.d("TAG_X", "Service started from play button")
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }()
        }
        pause_button.setOnClickListener {
            myPlayerService?.pausePlayer()
        }
        stop_button.setOnClickListener {
            if (myPlayerService != null) {
                unbindService(serviceConnection)
                myPlayerService = null

                //stopService(intent)
            }
        }
        audioSelected_imageview.setOnClickListener {
            myPlayerService?.playTracks(R.raw.relax)?: {
                Log.d("TAG_X", "Service started from play button")
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }()
        }
    }
    override fun onResume() {
        super.onResume()
        (audio_recyclerview.adapter as AudioAdapter?)?.submitList(audioList)

    }

    fun setUpRecyclerView(){
        audio_recyclerview.adapter = AudioAdapter(this)
        audio_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)


        val itemDecorator = DividerItemDecoration(this, LinearLayout.VERTICAL)
        audio_recyclerview.addItemDecoration(itemDecorator)
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
    }
}
