package br.com.dalla.hrc.actions;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.PersistenceException;

import java.math.BigDecimal;
import java.util.Locale;

public class atualizaItensColetas implements AcaoRotinaJava {


    @Override
    public void doAction(ContextoAcao ctx) throws Exception {
        Registro [] linha = ctx.getLinhas();


        if( linha.length == 0 ) {
            this.exibirErro("Selecione ao menos uma Linha");
        }


            for(Registro linhas:linha) {
                String status = (String) linhas.getCampo("STATUS");
                if (status.equals("A")) {
                    linhas.setCampo("STATUS", "C");
                    String msg;
                    ctx.setMensagemRetorno("Processo Concluido!");
            }
        }


        //String lote = (String) linha[0].getCampo("CONTROLE");

        //this.exibirErro("retonar minha variavel " + status + " " + lote);




    }

    private void exibirErro(String mensagem) throws Exception  {
        throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" height=\"100\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
    }
}
