@file:Suppress("DEPRECATION")

package e.darom.xylophone

/*
raw 리소스 디렉터리 추가
wav, mp3 등의 사운드 파일은 raw 리소스 디렉터리에 넣어서 사용합니다. 기본 생성되지 않기 때문에 추가해야 합니다.
res 폴더를 선택하고 마우스 오른쪽 버튼 클릭 또는 안드로이드 스튜디오 상단 메뉴에서 파일 -> 뉴 -> 안드로이드 리소스 디렉토리 를 클릭합니다.

음악파일을 모두 저장하고, 모든 파일을 선택한 후 Ctrl + c 를 눌러 클립보드에 복사합니다.
그 다음 raw 디렉터리를 선택하고 Ctrl + v 를 누르고 OK 를 클릭합니다.
 */
/*
안드로이드에서 소리를 재생하는 방법

대표적으로 MediaPlayer 클래스와 SoundPool 클래스를 사용하는 방법이 있습니다.
일반적인 소리 파일 연주에는 MediaPlayer 클래스를 사용합니다. 클래스 이름을 보면 알겠지만 음악 파일과 비디오 파일 모두 재생할 수 있습니다.
MediaPlayer 로 raw 디렉터리 파일을 재생하는 코드는 다음과 같이 간단합니다. 사용이 끝나면 반드시 release() 메서드를 호출하여 자원을 해제해야 합니다.

// raw 디렉터리의 do1 파일을 재생하는 예
val mediaPlayer = MediaPlayer.creat(this, R.raw.do1)
button.setOnClickListener{ mediaPlayer.start() }
...
// 사용이 끝나면 릴리즈해야 함
mediaPlayer.release()

MediaPlayer  클래스는 일반적으로 소리를 한 번만 재생하는 경우 또는 노래나 배경음과 같은 경우에는 유용합니다. 하지만 실로폰과 같이 연타를 해서 연속으로 소리를 재생하는 경우에는
SoundPool 클래스가 더 유용합니다.

SoundPool 클래스는 다음과 같이 사용합니다. Builder().build() 메서드로 SoundPool 객체를 생성하고 load() 메서드로 소리 파일을 로드하여 그 아이디를 반환합니다.

val soundPool = SoundPool.Builder().build()

val soundId = soundPool.load(this, R.raw.do1, 1)
button.setOnClickListener{ soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f) }


load() 메서드와 play() 메서드의 원형은 다음과 같습니다.
// 음원을 준비하여 id를 반환합니다.
load(context: Context, resId: Int, priority : Int) :
- context :  컨텍스트를 지정합니다. 액티비티를 지정합니다.
- resId : 재생할 raw 디렉터리의 소리 파일 리소스를 지정합니다.
- priority: 우선순위를 지정합니다. 숫자가 높으면 우선순위가 높습니다.

// 음원을 재생합니다.
 play(soundId: Int, leftVolume: Float, rightVolume: Float, priority: Int, loop: int, rate: Float) :
 - soundId : load() 메서드에서 반환된 음원의 id를 지정합니다.
 - leftVolume : 왼쪽 볼륨을 0.0 ~ 1.0 사이에서 지정합니다.
 - rightVolume : 오른쪽 볼륨을 0.0 ~ 1.0 사이에서 지정합니다.
 - priority : 우선순위를 지정합니다. 0이 가장 낮은 순위입니다.
 - loop : 반복을 지정합니다. 0이면 반복하지 않고 -1 이면 반복합니다.
 - rate : 재생 속도를 지정합니다. 1.0 이면 보통, 0.5 이면 0.5 배속, 2.0 이면 2배속입니다.

 실로폰은 연속으로 재생하는 경우이므로 SoundPool 클래스를 사용하겠습니다.
 */
/*
SoundPool 객체를 초기화하는 코드를 작성합니다.
setMaxStreams() 메서드는 한꺼번에 재생하는 음원 개수를 지정할 수 있습니다. 여기서는 음원 파일 개수에 맞춰 8개를 동시에 재생할 수 있게 했습니다.
load() 메서드로 음원 8개를 한 번에 로드할 수 있습니다.
 */

import android.media.SoundPool
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    /*
    private val soundPool = SoundPool.Builder().setMaxStreams(8).build()

    빨간 줄이 표시됩니다. 이 코드는 API 21(안드로이드 5.0) 이상부터 지원된다는 메시지가 표시됩니다.
    구 버전의 안드로이드 기기에서는 런타임 에러로 앱이 종료됩니다. SoundPool 클래스의 초기화 방법은 안드로이드 5.0 부터 변경되었습니다.
    그 이전에는 다음과 같은 코드를 사용했습니다. 생성자의 첫 번째 인자는 최대 재생 스트림 개수이고, 두 번쨰 인자는 어떤 종류의 음원인지를 지정합니다.
    세 번째 인자는 음질이며 0이 기본값입니다.

    private val soundPool = SoundPool(8, AudioManager.STREAM_MUSIC, 0)
    초기화 코드를 위와 같이 변경하면 에디터 창에는 생성자가 더 이상 사용되지 않는 다는 메시지가 출력됩니다.

    안드로이드 개발을 하다보면 API가 버전업되면서 생성자나 메서드에서 이런 경우를 자주 볼 수 있습니다.
    이때는 각 버전에서 추천되는 방식으로 동작하고 버전을 분기합니다.


    먼저 처음에 작성했던 코드에서 단축키 Alt + Enter 를 눌러 제안 사항을 표시합니다.
    Surround with 로 시작하는 항목을 클릭합니다. if 문으로 버전에 따른 분기를 하는 코드를 생성합니다.
    생성된 코드에 다음과 같이 구 버전용 SoundPool 객체 초기화 코드를 추가합니다. 자동으로 추가된 if문이 사용되어 롤리팝 이후와 이전에 다른 코드가 수행되게 되었습니다.
     */
    private val soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        SoundPool.Builder().setMaxStreams(8).build()
    } else {
        TODO("VERSION.SDK_INT < LOLLIPOP")
    }

    /*
    1. 먼저 listOf() 함수를 사용하여 텍스트 뷰의 ID와 음원 파일의 리소스 ID를 연관 지은 Pair 객체 8개를 리스트 객체 sounds 로 만듭니다.
    Pair 클래스는 두 개의 연관된 객체를 저장합니다.

    2. sounds 리스트를 forEach() 함수를 사용하여 요소를 하나씩 꺼내서 tune() 메서드에 전달합니다.
    3. tune() 메서드는 Pair 객체를 받아서 4. load() 메서드로 음원의 ID 를 얻고 5. findViewById() 메서드로 텍스트 뷰의 ID 에 해당하는 뷰를 얻고
    6. 텍스트 뷰를 클릭했을 때 음원을 재생합니다.

    7. 앱을 종료할 때는 반드시 release() 메서드를 호출하여 SoundPool 객체의 자원을 해제합니다.

    앱을 실행하여 소리가 잘 난다면 성공입니다.
     */
    private val sounds = listOf (    // 1.
        Pair(R.id.do1, R.raw.do1),
        Pair(R.id.re, R.raw.re),
        Pair(R.id.mi, R.raw.mi),
        Pair(R.id.fa, R.raw.fa),
        Pair(R.id.sol, R.raw.sol),
        Pair(R.id.la, R.raw.la),
        Pair(R.id.si, R.raw.si),
        Pair(R.id.do2, R.raw.do2)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        /*
        화면이 가로 모드로 고정되게 하기
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        새로운 방법 : 매니페스트 파일에서 액티비티의 속성 설정하기.
        <activity android:name=".MainActivity" android:screenOrientation="landscape">
         */

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sounds.forEach{ tune(it)}   // 2.
    }

    private fun tune(pitch: Pair<Int, Int>){    // 3.
        val soundId = soundPool.load(this, pitch.second, 1)     // 4.
        findViewById<TextView>(pitch.first).setOnClickListener{                // 5.
            soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f) //6.
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()     // 7.
    }
}
