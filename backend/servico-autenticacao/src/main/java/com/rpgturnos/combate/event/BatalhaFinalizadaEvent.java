package com.rpgturnos.combate.event;

import java.io.Serializable;

public class BatalhaFinalizadaEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long batalhaId;
    private Long usuarioId;
    private Long personagemId;
    private String inimigoNome;
    private String resultado;
    private Integer vidaFinalPersonagem;

    public Long getBatalhaId() {
        return batalhaId;
    }

    public void setBatalhaId(Long batalhaId) {
        this.batalhaId = batalhaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getPersonagemId() {
        return personagemId;
    }

    public void setPersonagemId(Long personagemId) {
        this.personagemId = personagemId;
    }

    public String getInimigoNome() {
        return inimigoNome;
    }

    public void setInimigoNome(String inimigoNome) {
        this.inimigoNome = inimigoNome;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public Integer getVidaFinalPersonagem() {
        return vidaFinalPersonagem;
    }

    public void setVidaFinalPersonagem(Integer vidaFinalPersonagem) {
        this.vidaFinalPersonagem = vidaFinalPersonagem;
    }
}
