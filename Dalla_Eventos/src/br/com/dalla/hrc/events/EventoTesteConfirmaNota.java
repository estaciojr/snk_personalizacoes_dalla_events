package br.com.dalla.hrc.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.event.ModifingFields;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.*;
import br.com.sankhya.modelcore.comercial.centrais.CACHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;
import com.sankhya.util.BigDecimalUtil;

import java.math.BigDecimal;

public class EventoTesteConfirmaNota implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        EventoTeste(persistenceEvent);
    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext ctx) throws Exception {
    }

    private void EventoTeste(PersistenceEvent persistenceEvent) throws Exception {

        DynamicVO newCabVO = (DynamicVO) persistenceEvent.getVo();
        DynamicVO oldCabVO = (DynamicVO) persistenceEvent.getOldVO();
        BigDecimal nuNota =  oldCabVO.asBigDecimal("NUNOTA");

        //DynamicVO newCabVO = ConfirmacaoNotaHelper.buildPrePersistState(nuNota).getNewVO();
        //DynamicVO oldCabVO = ConfirmacaoNotaHelper.buildPrePersistState(nuNota).getOldVO();

        if ((!oldCabVO.asString("STATUSNOTA").equals("L")) &&
                (newCabVO.asString("STATUSNOTA").equals("L")))
        {
            this.exibirErro("Confirmando a  Nota:" + nuNota);
        }

}



    
    private void exibirErro(String mensagem) throws Exception  {
        throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\" height=\"100\" width=\"300\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"15\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
    }
}
