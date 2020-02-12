package netty;

/**
 * @author iceWang
 * @date 2020/1/17
 * @description
 */
public class Home {


    public static void main(String[] args) {
        Father father = new Father();
        Son son = new Son();
        father.setSon(son);
        son.setFather(father);
        Home home = new Home();
        System.out.println(home);
    }
}
