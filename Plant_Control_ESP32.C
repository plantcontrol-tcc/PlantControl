#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64

#define OLED_RESET -1
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);

// --- CONFIGURAÇÃO DOS PINOS ---
const int ledSeco = 14;       // LED: RUIM (Terra Seca)
const int ledIdeal = 13;      // LED: BOM (Humidade Ideal)
const int ledEncharcado = 12; // LED: RUIM (Excesso de Água)

const int pinoSensor = 25;    // Sensor de humidade (Analógico)
const int pinoRele = 26;      // Módulo Relé da Bomba (Ativa em LOW)

// --- NOVOS PINOS ---
const int pinoBoia = 15;      // Sensor de Boia (GND + Pino 15)
const int pinoBotaoM = 16;    // Botão Ativação Manual (GND + Pino 16)

// Variáveis de controlo de tempo
unsigned long tempoAnteriorLeitura = 0;
const long intervaloLeitura = 400;  

// Flags de status
bool bombaAutomatica = false; // Guarda se a automação quer ligar a bomba
bool bombaEstadoFinal = false; // Estado real atual da bomba (Ligada/Desligada)
bool temAguaNoReservatorio = true;

void setup() {
  Serial.begin(115200);

  // Configuração das saídas
  pinMode(ledSeco, OUTPUT);
  pinMode(ledIdeal, OUTPUT);
  pinMode(ledEncharcado, OUTPUT);
  pinMode(pinoRele, OUTPUT);
  digitalWrite(pinoRele, HIGH); // Garante relé iniciado DESLIGADO (Active-LOW)

  // Configuração das entradas com resistor Pull-up interno (Ativam em LOW)
  pinMode(pinoBoia, INPUT_PULLUP);
  pinMode(pinoBotaoM, INPUT_PULLUP);

  // Inicializa o Ecrã OLED (Pinos 5 e 4)
  Wire.begin(5, 4);
  if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println("OLED não encontrado");
    while (true);
  }

  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(15, 25);
  display.println("SISTEMA PROTEGIDO");
  display.display();
  delay(1500);
}

void loop() {
  unsigned long tempoAtual = millis();

  if (tempoAtual - tempoAnteriorLeitura >= intervaloLeitura) {
    tempoAnteriorLeitura = tempoAtual;
    
    // 1. LEITURA DOS SENSORES E BOTÕES
    int valorPuro = analogRead(pinoSensor);
    int percentagemHumidade = map(valorPuro, 4095, 0, 0, 100);
    percentagemHumidade = constrain(percentagemHumidade, 0, 100);

    // Boia fecha curto (LOW) quando cheia. Se aberta (HIGH), acabou a água.
    temAguaNoReservatorio = (digitalRead(pinoBoia) == LOW);
    
    // Botão manual pressionado joga o pino para LOW
    bool botaoManualPressionado = (digitalRead(pinoBotaoM) == LOW);

    // 2. ATUALIZAÇÃO DOS LEDS DE STATUS DO SOLO
    if (percentagemHumidade < 30) {
      digitalWrite(ledSeco, HIGH);  digitalWrite(ledIdeal, LOW);   digitalWrite(ledEncharcado, LOW);
    } else if (percentagemHumidade >= 30 && percentagemHumidade <= 75) {
      digitalWrite(ledSeco, LOW);   digitalWrite(ledIdeal, HIGH);  digitalWrite(ledEncharcado, LOW);
    } else {
      digitalWrite(ledSeco, LOW);   digitalWrite(ledIdeal, LOW);   digitalWrite(ledEncharcado, HIGH);
    }

    // 3. LÓGICA DE CONTROLE DA BOMBA (HISTERESE AUTOMÁTICA)
    if (percentagemHumidade < 30) {
      bombaAutomatica = true;  // Solo seco: precisa regar
    } else if (percentagemHumidade >= 70) {
      bombaAutomatica = false; // Solo úmido o suficiente: para de regar
    }

    // 4. REGRA DE OURO E EXECUÇÃO (SEGURANÇA)
    if (!temAguaNoReservatorio) {
      // Sem água: Desliga imediatamente por segurança física da bomba
      digitalWrite(pinoRele, HIGH); 
      bombaEstadoFinal = false;
    } 
    else if (botaoManualPressionado) {
      // Tem água e botão pressionado: Força ligada
      digitalWrite(pinoRele, LOW);
      bombaEstadoFinal = true;
    } 
    else {
      // Tem água e sem botão: Segue o curso da automação
      digitalWrite(pinoRele, bombaAutomatica ? LOW : HIGH);
      bombaEstadoFinal = bombaAutomatica;
    }

    // 5. ATUALIZA O OLED
    atualizarEcra(valorPuro, percentagemHumidade, botaoManualPressionado);
  }
}

void atualizarEcra(int valorPuro, int percentagem, bool manual) {
  display.clearDisplay();
  
  // Linha superior: Estado do Reservatório / Modo
  display.setTextSize(1);
  display.setCursor(0, 0);
  if (!temAguaNoReservatorio) {
    display.print("RESERV: VAZIO! [!]");
  } else if (manual) {
    display.print("MODO: MANUAL FORCADO");
  } else {
    display.print("MODO: AUTOMATICO");
  }
  display.drawFastHLine(0, 10, 128, SSD1306_WHITE);

  // Dados do Sensor
  display.setCursor(0, 16);
  display.print("ADC: "); display.print(valorPuro);
  
  display.setCursor(75, 16);
  display.print("Bomba:"); display.print(bombaEstadoFinal ? "ON" : "OFF");

  // Humidade Grande
  display.setTextSize(2);
  display.setCursor(0, 32);
  display.print("Hum: "); display.print(percentagem); display.print("%");

  // Status de segurança na última linha
  display.setTextSize(1);
  display.setCursor(0, 54);
  if (!temAguaNoReservatorio) {
    display.print("Bomba Bloqueada!");
  } else if (manual) {
    display.print("Regando via Botao");
  } else if (percentagem < 30) {
    display.print("Status: Seco (Regando)");
  } else if (percentagem >= 30 && percentagem <= 75) {
    display.print("Status: Solo Ideal");
  } else {
    display.print("Status: Encharcado");
  }

  display.display();
}