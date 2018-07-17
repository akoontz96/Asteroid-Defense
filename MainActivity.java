package com.example.me.asteroiddefense;

import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    Timer timer = new Timer();
    TextView score;
    ImageView building1, building2, building3, building4, rubble1, rubble2, rubble3, rubble4, gun, projectile, asteroid;
    SoundPool soundPool, soundPool2;
    AlertDialog.Builder start, end;
    TimerTask updateGame;
    int explosion, explosion2;
    int rotation = 0;
    int rotate = 0;
    int projectileX, projectileY, asteroidX, asteroidY, screenX, screenY;
    int projectileSpeed = 15;
    int asteroidSpeed = 5;
    int currScore = 0;
    int X = 0;
    boolean fired = false;
    boolean asteroidStart = false;

    Random r = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        projectileX = 525;
        projectileY = 1600;
        asteroidX = 525;
        asteroidY = -200;
        X = r.nextInt(2);

        score = (TextView) findViewById(R.id.score);
        building1 = (ImageView)findViewById(R.id.building1);
        building2 = (ImageView)findViewById(R.id.building2);
        building3 = (ImageView)findViewById(R.id.building3);
        building4 = (ImageView)findViewById(R.id.building4);
        rubble1 = (ImageView) findViewById(R.id.rubble1);
        rubble2 = (ImageView) findViewById(R.id.rubble2);
        rubble3 = (ImageView) findViewById(R.id.rubble3);
        rubble4 = (ImageView) findViewById(R.id.rubble4);
        projectile = (ImageView) findViewById(R.id.projectile);
        asteroid = (ImageView) findViewById(R.id.asteroid);
        gun = (ImageView) findViewById(R.id.gun);

        asteroid.setVisibility(View.INVISIBLE);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenX = size.x;
        screenY = size.y;

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
        soundPool2 = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
        AssetManager assMan = getAssets();

        try {
            AssetFileDescriptor descriptor = assMan.openFd("Explosion+1.wav");
            explosion = soundPool.load(descriptor, 1);
            descriptor = assMan.openFd("Torpedo+Explosion.wav");
            explosion2 = soundPool2.load(descriptor, 1);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        end = new AlertDialog.Builder(this);
        start = new AlertDialog.Builder(this);
        start.setTitle("Asteroid Defense");
        start.setMessage("You must prevent the asteroids from hitting the city by shooting them out of the sky! Use the arrows to aim and the Fire button to shoot!");
        start.setNeutralButton("Ok, I'm Ready!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                asteroid.setVisibility(View.VISIBLE);
                asteroidStart = true;
            }
        });

        end.setTitle("Game Over");
        end.setNeutralButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                currScore = 0;

                building1.setVisibility(View.VISIBLE);
                rubble1.setVisibility(View.INVISIBLE);
                building2.setVisibility(View.VISIBLE);
                rubble2.setVisibility(View.INVISIBLE);
                building3.setVisibility(View.VISIBLE);
                rubble3.setVisibility(View.INVISIBLE);
                building4.setVisibility(View.VISIBLE);
                rubble4.setVisibility(View.INVISIBLE);
                asteroid.setVisibility(View.INVISIBLE);

                projectileX = 525;
                projectileY = 1600;
                asteroidX = 525;
                asteroidY = -200;
                asteroid.setX(asteroidX);
                asteroid.setY(asteroidY);
                asteroidSpeed = 5;

                asteroidStart = false;
                start.show();
            }
        });

        start.show();
        final int FPS = 60;
        updateGame = new UpdateGameTask();
        timer.scheduleAtFixedRate(updateGame, 0, 1000 / FPS);
    }

    class UpdateGameTask extends TimerTask {

        @Override
        public void run() {

            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if(asteroid.getVisibility() == View.VISIBLE){
                        currScore += 1;
                        score.setText("Score: " + currScore);
                    }

                    if (X == 0){
                        asteroidX += 1;
                    }
                    else{
                        asteroidX += -1;
                    }

                    if(asteroidStart == true) {
                        asteroidY += (asteroidSpeed * 1);
                        asteroid.setX(asteroidX);
                        asteroid.setY(asteroidY);
                    }

                    if (projectileY >= asteroidY && projectileY <= asteroidY + 150 && projectileX >= asteroidX && projectileX <= asteroidX + 150){
                        asteroidSpeed += 1;
                        currScore += 100;
                        soundPool.play(explosion, 1, 1, 0, 0, 1);
                        asteroidX = r.nextInt(800) + 100;
                        asteroidY = -200;
                        fired = false;
                        projectile.setX(525);
                        projectile.setY(1600);
                        projectileX = 525;
                        projectileY = 1600;
                        X = r.nextInt(2);
                    }

                    if (asteroidY > building1.getTop()){
                        if (asteroid.getVisibility() == View.VISIBLE){
                            soundPool2.play(explosion2, 1, 1, 0, 0, 1);
                        }

                        end.setMessage("An asteroid reached the ground and destroyed the city!\n You're final score was: " + currScore);

                        building1.setVisibility(View.INVISIBLE);
                        rubble1.setVisibility(View.VISIBLE);
                        building2.setVisibility(View.INVISIBLE);
                        rubble2.setVisibility(View.VISIBLE);
                        building3.setVisibility(View.INVISIBLE);
                        rubble3.setVisibility(View.VISIBLE);
                        building4.setVisibility(View.INVISIBLE);
                        rubble4.setVisibility(View.VISIBLE);
                        asteroid.setVisibility(View.INVISIBLE);

                        asteroidX = 525;
                        asteroidY = -200;
                        asteroid.setX(asteroidX);
                        asteroid.setY(asteroidY);
                        asteroidStart = false;

                        end.show();
                    }

                    if (fired == true){
                        projectileX += (projectileSpeed * rotate/20);
                        projectileY += (projectileSpeed * -1);
                        projectile.setX(projectileX);
                        projectile.setY(projectileY);

                        if ((projectileX + projectile.getWidth()) > screenX || projectileX < 0){
                            fired = false;
                            projectile.setX(525);
                            projectile.setY(1600);
                            projectileX = 525;
                            projectileY = 1600;
                        }
                        if ((projectileY + projectile.getHeight()) > screenY || projectileY < 0){
                            fired = false;
                            projectile.setX(525);
                            projectile.setY(1600);
                            projectileX = 525;
                            projectileY = 1600;
                        }
                    }
                }
            });
        }
    }


    public void moveLeft(View view){
        if(gun.getRotation() > -30) {
            gun.setRotation(rotation += -10);
        }
    }

    public void moveRight(View view){
        if (gun.getRotation() < 30){
            gun.setRotation(rotation += 10);
        }
    }

    public void fire(View view){
        fired = true;
        projectile.setRotation(rotation);
        rotate = rotation;
    }
}
