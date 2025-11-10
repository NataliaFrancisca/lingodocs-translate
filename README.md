# Lingodocs

Este projeto tem como objetivo realizar a tradução automática de arquivos `.txt` enviados para um bucket S3, utilizando uma função Lambda escrita em Java.

Quando um arquivo é adicionado no bucket, a função Lambda é acionada automaticamente, traduz o conteúdo e salva a versão traduzida em outra pasta do mesmo bucket.

## Funcionamento
1. O usuário adiciona um arquivo `.txt` na pasta `/inbound` do bucket S3.
2. Um evento do S3 aciona a função Lambda.
3. A Lambda lê o arquivo e envia o texto para tradução via API do Gemini.
4. O texto traduzido é salvo na pasta `/outbound` do mesmo bucket.

## Regra de Tradução
- Se o texto estiver em **Português**, será traduzido para **Inglês**.
- Se o texto estiver em **qualquer outro idioma**, será traduzido para **Português**.
- Essa lógica pode ser alterada na classe `GeminiService`.
   
## Tecnologias
- Java
- Maven
- AWS Lambda
- AWS S3
- Gemini API

## Aprendizados
Durante o desenvolvimento deste projeto, aprendi:
- Como configurar uma função **AWS Lambda** para consumir eventos de um **bucket S3**.
- Como fazer upload e leitura de arquivos no S3 usando eventos de trigger.
- Como integrar a Lambda com uma API externa (Gemini).
- Como usar o **CloudWatch** para monitorar logs da função.


