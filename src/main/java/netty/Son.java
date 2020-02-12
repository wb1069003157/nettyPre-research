package netty;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description
 */
public class Son {
    private String name;
    private Father father;

    public Son() {
    }

    public Son(String name, Father father) {
        this.name = name;
        this.father = father;
    }

    public void setFather(Father father) {
        this.father = father;
    }
}
