package game.services

import javazoom.jl.player.Player

import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

class AudioService {
    private Clip menuWavClip
    private Player battleMp3Player
    private volatile boolean loopBattleMusic = false
    private Thread battleMusicThread
    private final Class resourceOwner

    AudioService(Class resourceOwner) {
        this.resourceOwner = resourceOwner
    }

    void playMenuMusic(String path) {
        stopAllMusic()
        Thread.start {
            try {
                InputStream input = openResource(path)
                if (input == null) {
                    return
                }

                AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(input))
                menuWavClip = AudioSystem.getClip()
                menuWavClip.open(audio)
                menuWavClip.loop(Clip.LOOP_CONTINUOUSLY)
                menuWavClip.start()
            } catch (Exception e) {
                println "Erro ao tocar musica do menu: ${e.message}"
            }
        }
    }

    void playBattleMusic(String path) {
        stopAllMusic()
        loopBattleMusic = true

        battleMusicThread = Thread.start {
            while (loopBattleMusic) {
                try {
                    InputStream input = openResource(path)
                    if (input != null) {
                        BufferedInputStream bis = new BufferedInputStream(input)
                        battleMp3Player = new Player(bis)
                        battleMp3Player.play()
                        bis.close()
                    } else {
                        Thread.sleep(500)
                    }
                } catch (Exception e) {
                    try {
                        Thread.sleep(500)
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    void playSfx(String path) {
        Thread.start {
            try {
                InputStream input = openResource(path)
                if (input == null) {
                    return
                }

                BufferedInputStream bis = new BufferedInputStream(input)
                if (path.toLowerCase().endsWith('.wav')) {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(bis)
                    Clip sfxClip = AudioSystem.getClip()
                    sfxClip.open(audioStream)
                    sfxClip.start()
                } else {
                    Player sfxPlayer = new Player(bis)
                    sfxPlayer.play()
                }
            } catch (Exception e) {
                println "Erro SFX: ${e.message}"
            }
        }
    }

    void stopAllMusic() {
        loopBattleMusic = false

        if (menuWavClip != null) {
            try {
                if (menuWavClip.running) {
                    menuWavClip.stop()
                }
                menuWavClip.close()
            } catch (Exception ignored) {
            }
            menuWavClip = null
        }

        if (battleMp3Player != null) {
            try {
                battleMp3Player.close()
            } catch (Exception ignored) {
            }
            battleMp3Player = null
        }

        if (battleMusicThread != null) {
            try {
                battleMusicThread.interrupt()
            } catch (Exception ignored) {
            }
            battleMusicThread = null
        }
    }

    private InputStream openResource(String path) {
        String normalized = path.replace('src/main/resources/', '')
        InputStream input = resourceOwner.getResourceAsStream("/${normalized}")
        if (input != null) {
            return input
        }

        File file = new File(path)
        file.exists() ? new FileInputStream(file) : null
    }
}
