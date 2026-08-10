# 📊 StatsCalculator - Calculadora Estatística para Android

O **StatsCalculator** é um aplicativo Android nativo desenvolvido em **Kotlin** e **Jetpack Compose**, projetado para realizar cálculos estatísticos descritivos, probabilísticos e inferenciais com alta precisão numérica e visualização gráfica vetorial em tempo real.

---

## 🚀 Funcionalidades Principais

### 1. Distribuições de Probabilidade
* **Distribuição Normal $N(\mu, \sigma^2)$:** Cálculo de probabilidades acumuladas ($P(X \le x)$, $P(X \ge x)$, $P(x_1 \le X \le x_2)$), $Z$-scores e renderização vetorial da curva de Gauss.
* **Distribuição de Poisson:** Cálculo de PMF e CDF para taxa média $\lambda$.
* **Distribuição Binomial:** Probabilidade exata e acumulada para $n$ ensaios e probabilidade $p$.

### 2. Inferência Estatística
* **Intervalos de Confiança:** Estimativa pontual, erro padrão ($SE$) e margem de erro ($E$) para Média Populacional ($\mu$) e Proporção ($p$), acompanhados de régua vetorial do intervalo.
* **Testes de Hipóteses ($Z$-test):** Testes bilaterais e unilaterais (à esquerda/direita) para Média e Proporção com cálculo do valor-$p$, região crítica de rejeição e renderização visual do ponto de corte.

---

## 🛠️ Arquitetura e Engenharia de Software

O aplicativo foi projetado seguindo os princípios de **Clean Architecture** e o padrão **MVVM (Model-View-ViewModel)**:

* **Centralização Matemática (`StatisticalUtils`):** Toda a lógica estatística e fórmulas numéricas estão isoladas em um objeto puro do Kotlin, desacoplado de componentes visuais do Android.
* **Desenho Vetorial Customizado (`Canvas`):** Gráficos interativos (Curva Normal, Intervalos de Confiança e Distribuição $Z$) desenhados usando a API `Canvas` nativa do Jetpack Compose.
* **Estado Reativo (`StateFlow`):** Fluxo de dados unidirecional entre ViewModel e Composables garantindo persistência durante rotação de tela.

---

## 🧪 Suíte de Testes Unitários

O projeto conta com cobertura de testes unitários automatizados com **JUnit 4**, cobrindo:
* Validação contra tabelas estatísticas de referência (Tabela $Z$).
* Casos de borda (*edge cases*) e prevenção de estouro de precisão numérica ($overflow/underflow$).
* Testes de estado em `InferentialViewModel`.

Para executar a suíte de testes via linha de comando:

```bash
./gradlew test