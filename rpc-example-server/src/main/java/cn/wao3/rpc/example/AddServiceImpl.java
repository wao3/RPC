package cn.wao3.rpc.example;

/**
 * @author wangao
 * @date 2021-05-19
 */
public class AddServiceImpl implements AdderService{

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
