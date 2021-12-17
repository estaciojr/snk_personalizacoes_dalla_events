package br.com.dalla.hrc.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;

public class getPropertyFunc implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent evet) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent evet) throws Exception {
        getPropertyFunc(evet);
    }

    @Override
    public void beforeDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent evet) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent evet) throws Exception {

    }

    @Override
    public void afterDelete(PersistenceEvent evet) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }

    private void getPropertyFunc(PersistenceEvent evet) throws Exception {
        DynamicVO testVO = (DynamicVO) evet.getVo();
    }
}
