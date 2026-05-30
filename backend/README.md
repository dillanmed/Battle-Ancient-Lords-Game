# JogoRPG-API

## Supabase pooler

Os tres microservicos usam o pooler do Supabase em modo session. Como esse modo limita
clientes por `pool_size`, cada servico Spring Boot precisa ter um pool Hikari pequeno.

Por padrao, os perfis PostgreSQL/Supabase usam:

- `DB_POOL_MAX_SIZE=4`
- `DB_POOL_MIN_IDLE=0`

Com os tres servicos rodando ao mesmo tempo, isso limita o total a 12 conexoes e evita
o erro `FATAL: (EMAXCONNSESSION) max clients reached in session mode`.

