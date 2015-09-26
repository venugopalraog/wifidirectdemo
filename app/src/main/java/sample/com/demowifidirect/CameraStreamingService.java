package sample.com.demowifidirect;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * Created by Vgubbala on 9/25/15.
 */
public class CameraStreamingService extends IntentService {
    public static final String TAG ="CameraStreamingService";

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_STREAM_VIDEO = "sample.com.demowifidirect.STREAM_VIDEO";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

    private Camera mCamera = null;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CameraStreamingService(String name) {
        super(name);
    }

    public CameraStreamingService() {
        super("CameraStreamingService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_STREAM_VIDEO)) {
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            try {
                Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                Log.d(WiFiDirectActivity.TAG, "Client socket - " + socket.isConnected());

                ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(socket);

                mCamera = CameraHelper.getDefaultCameraInstance();

                MediaRecorder recorder = new MediaRecorder();
                mCamera.unlock();
                recorder.setCamera(mCamera);
                recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
              //  recorder.setOutputFile(pfd.getFileDescriptor());
                recorder.setOutputFile("/sdcard/videocapture_example.3gp");
                recorder.setVideoFrameRate(20);
                recorder.setVideoSize(176, 144);
                recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
                MyApplication.getBus().post(recorder);
//                recorder.setPreviewDisplay(mHolder.getSurface());
                try {
                    recorder.prepare();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                recorder.start();
                /*
                mCamera = getCameraInstance();
                mMediaRecorder = new MediaRecorder();
                mCamera.unlock();
                mMediaRecorder.setCamera(mCamera);
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
// this is the unofficially supported MPEG2TS format, suitable for streaming (Android 3.0+)
                mMediaRecorder.setOutputFormat(8);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
                mediaRecorder.setOutputFile(pfd.getFileDescriptor());
                mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
                mMediaRecorder.prepare();
                mMediaRecorder.start();
                OutputStream stream = socket.getOutputStream();*/
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    private void getCameraInstance() {


    }
}
