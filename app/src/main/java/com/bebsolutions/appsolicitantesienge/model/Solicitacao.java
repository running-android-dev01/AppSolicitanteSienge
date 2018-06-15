package com.bebsolutions.appsolicitantesienge.model;

import com.j256.ormlite.field.DatabaseField;

import java.util.HashMap;

public class Solicitacao {
    @DatabaseField(id = true)
    public String id;


    @DatabaseField
    public String solicitanteAuth;

    @DatabaseField
    public String solicitante;

    @DatabaseField
    public String solicitanteEmail;

    @DatabaseField
    public String solicitanteTelefone;

    @DatabaseField
    public String solicitanteData;

    @DatabaseField
    public String descricao;

    @DatabaseField
    public String fotografia;

    @DatabaseField
    public String melhorHorario;

    @DatabaseField
    public String solicitacaoStatus;

    @DatabaseField
    public String solicitacaoStatusDescricao;


    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("solicitante", solicitante);
        map.put("solicitanteEmail", solicitanteEmail);
        map.put("solicitanteTelefone", solicitanteTelefone);
        map.put("solicitanteData", solicitanteData);
        map.put("descricao", descricao);
        map.put("fotografia", fotografia);
        map.put("melhorHorario", melhorHorario);
        map.put("solicitacaoStatus", solicitacaoStatus);
        map.put("solicitacaoStatusDescricao", solicitacaoStatusDescricao);

        return map;
    }

    public void getMap(String key, HashMap<String, Object> map){
        id = key;
        solicitante = map.get("solicitante").toString();
        solicitanteEmail = map.get("solicitanteEmail").toString();
        solicitanteTelefone = map.get("solicitanteTelefone").toString();
        solicitanteData = map.get("solicitanteData").toString();
        descricao = map.get("descricao").toString();
        fotografia = map.get("fotografia").toString();
        melhorHorario = map.get("melhorHorario").toString();
        solicitacaoStatus = map.get("solicitacaoStatus").toString();
        solicitacaoStatusDescricao = map.get("solicitacaoStatusDescricao").toString();
    }
}
