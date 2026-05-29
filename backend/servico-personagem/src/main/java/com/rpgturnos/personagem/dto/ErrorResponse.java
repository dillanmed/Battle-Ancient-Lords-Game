package com.rpgturnos.personagem.dto;

public class ErrorResponse {

    private final int status;
    private final String erro;
    private final String mensagem;

    public ErrorResponse(int status, String erro, String mensagem) {
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
    }

    public int getStatus() {
        return status;
    }

    public String getErro() {
        return erro;
    }

    public String getMensagem() {
        return mensagem;
    }
}
