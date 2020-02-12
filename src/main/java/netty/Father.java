package netty;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description
 */
public class Father {
    private String name;
    private Son son;

    public Father() {
    }

    public Father(String name, Son son) {
        this.name = name;
        this.son = son;
    }

    public void setSon(Son son) {
        this.son = son;
    }
}
