package com.followme.model;

public class Group {
	private int id;
	private String nome, descricao, foto_patch;
 
    public Group() {
    }

	public Group(int id, String nome, String descricao, String foto_patch) {
    	this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.foto_patch = foto_patch;
    }

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getFoto_patch() {
		return foto_patch;
	}

	public void setFoto_patch(String foto_patch) {
		this.foto_patch = foto_patch;
	}
 
	 public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
 
}