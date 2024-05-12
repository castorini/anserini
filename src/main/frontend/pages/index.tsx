/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
