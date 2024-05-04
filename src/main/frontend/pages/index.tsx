import type { NextPage } from 'next'
import Head from 'next/head'
import SearchBar from './components/SearchBar'

const Home: NextPage = () => {
  return (
    <div>
      <Head>
        <title>TEST APP</title>
        <meta name="description" content="Anserini demo for text retrieval" />
        <link rel="icon" href="/favicon.ico" />
      </Head>

      <main>
        <h1>
          Welcome to <a href="https://github.com/castorini/anserini/">Anserini!</a>
        </h1>

        <SearchBar />

      </main>
    </div>
  )
}

export default Home
