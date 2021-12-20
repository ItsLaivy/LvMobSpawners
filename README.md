<p align="center">
<img src="https://user-images.githubusercontent.com/87931201/146769410-fce8290c-be91-45ef-940d-5747f6b81f92.png">
</p>
<hr/>

<h2>Comandos</h2>
<i>/spawners (spawner, mobspawner, mobspawners)</i> | A loja de spawners do plug-in<br>

<h2>Dependências</h2>
<i>- Vault</i> | Para a loja de spawners<br>
<i>- Qualquer plugin de economia</i> | Para o funcionamento do Vault

<h1>Imagens do Plug-in</h1>

<details>
	<summary>Em funcionamento</summary>
	<img src="https://user-images.githubusercontent.com/87931201/146770351-4b2492a3-6651-452a-8c3f-f5f2867b5640.gif">
</details>
<details>
	<summary>Gerenciador único de spawners</summary>
	<img src="https://user-images.githubusercontent.com/87931201/146770813-77a7fbd2-6941-4c05-9e7b-c5cb931d92cd.gif">
</details>
<details>
	<summary>Múltiplo gerenciamento de spawners</summary>
	<img src="https://user-images.githubusercontent.com/87931201/146771052-3ead42b1-3c9c-4681-8e30-446a5bcdb1c1.gif">
</details>
<details>
	<summary>Criação de spawners</summary>
	<img src="https://user-images.githubusercontent.com/87931201/146771135-5211d9dc-f555-430b-a1f8-0c1509de32ed.png">
</details>

<h1>Wiki</h1>

<details>
	<summary>Como criar um novo tipo de spawner</summary>
	Siga esse exemplo abaixo:<br>
	<img src="https://user-images.githubusercontent.com/87931201/146771471-9c8965f2-dbe2-4d4e-ba68-ed0ff99526e7.gif">
</details>
<details>
	<summary>Como alterar a textura dos spawners</summary>
	Acesse o site <a href="https://minecraft-heads.com/custom-heads">Minecraft-Heads</a> e busque pela cabeça desejada...<br><br>
	<img src="https://user-images.githubusercontent.com/87931201/146771867-a1e9edbc-699c-43f8-85c3-7f9d1faff7f7.png"><br><br>
	Depois disso, procure pelo `Value` da cabeça, é esse valor que nos dirá a textura da cabeça... `(normalmente esse valor fica no final da página da cabeça)`<br><br>
	<img src="https://user-images.githubusercontent.com/87931201/146771989-bb9450dd-bd59-43cb-9370-4795c865a9d6.png"><br><br>
	Copie esse valor, e cole na seção `spawner head texture` do seu spawner, tipo assim:<br><br>
	<img src="https://user-images.githubusercontent.com/87931201/146772329-7d89aae9-4597-44ff-af7a-de324f8af802.png"><br><br>
	E pronto!<br><br>
	<img src="https://user-images.githubusercontent.com/87931201/146772451-4ec72538-abd8-4a08-bb3a-ba4ec8d9a96a.png">
</details>
<details>
	<summary>Algumas opções da configuração...</summary>
	<img src="https://user-images.githubusercontent.com/87931201/146772624-e4fd5f6c-f40b-4230-a01b-5b4f428b8619.png"> Representa o tamanho do spawner, ele terá um tamanho reduzido caso seja "false".<br><br>
	<img src="https://user-images.githubusercontent.com/87931201/146772775-7acd589e-c72e-4a41-8b9e-71325654ed29.png"> Esse é o raio que ele procurará por monstros, no caso da print, ele procurará por monstros em um raio de 100 blocos (para todas as direções), e se conter mais animais do que o limite permitido (level de "Mobs em Área" do spawner) não nascerá mais enquanto não estiver dentro do limite novamente.<br><br>
</details>