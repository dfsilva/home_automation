package br.com.diegosilva.automation;

import akka.actor.typed.ActorSystem;
import com.typesafe.config.ConfigFactory;

public class Main {
    public static void main(String[] args){
        ActorSystem.create(Guardian.create(), "Automation", ConfigFactory.load());
    }
}
