package app.controller;

import app.models.domains.Pessoa;
import system.controller.Controller;
import system.controller.Result;

/**
 *
 * @author leodouglas
 */
public class SampleController extends Controller {

    public Result index() {
        session.add("pessoa", new Pessoa("Léo Padilha", 25));
        viewData.add("pessoa", session.get("pessoa"));
        return renderView("main/index");
    }

    public Result json() {
        viewData.add("pessoa", session.get("pessoa"));
        return renderJSON();
    }
    
    public Result xml() {
        viewData.add("pessoa", session.get("pessoa"));
        return renderXML("values");
    }

    public Result login() {
        return renderHtml("<html><body><h1>Bem vindo ao login!</h1></body></html>");
    }

    public Result client(String name) {
        return renderHtml("<html><body><h1>" + name + "</h1></body></html>");
    }
    
    public Result client(String name, String idade) {
        return renderHtml("<html><body><h1>" + name + ", " + idade +  "</h1></body></html>");
    }
    
    public Result download() {
        return renderResFile("img/paladinu.jpg");
    }    
}
