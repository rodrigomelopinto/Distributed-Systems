# Principal desafio da 3ª Fase

Na 3ªfase as operações de inscrição podem ser feitas por todos os servidores, isto causa um problema de ter informação incoerente entre servidores. Na situação em que dois alunos tentam inscrever-se concorrentemente e excede a capacidade da turma o que deve acontecer?

## Solução implementada

Para resolver este problema a nossa solução foi enviar o tempo em que foi efetuada cada inscrição para o servidor que localmente guarda uma *lista* com todos os updates. De seguida quando ocorre o gossip o servidor compara a sua lista de updates com a que recebeu e caso o total de inscrições exceda a capacidade, coloca as inscrições que estão em ambas as listas de updates por ordem temporal até à capacidade, as excedentes são descartadas. 
Caso duas inscrições tenham sido perfeitamente concorrentes dá prioridade ao **primário**.

Outra situação que pode causar incoerências acontece quando o professor fecha inscrições no primário e tenha sido efetuado uma inscrição no secundário antes de o gossip acontecer. Para resolver este problema adicionámos uma variável *closeTime* que é atualizada com o tempo em que o comando closeEnrollments acontece, depois quando o servidor primário recebe um gossip compara o tempo em que a inscrição foi efetuada com este e decide se a aceita ou não.
A nossa solução resume-se a uma abordagem semelhante ao **_Bayou_** pois resolve os conflitos de acordo com uma política específica à nossa aplicação que é o tempo de relógio físico.

### Parte Bónus

A nossa solução também suporta um servidor primário e vários secundários, no entanto o gossip é efetuado apenas de primário para secundários e de secundário para primário, fazendo com que os secundários não tenham de decidir entre eles em caso de empate temporal.