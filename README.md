Anserini <img src="docs/anserini-logo.png" width="300" />
========
[![build](https://github.com/castorini/anserini/actions/workflows/maven.yml/badge.svg)](https://github.com/castorini/anserini/actions)
[![codecov](https://codecov.io/gh/castorini/anserini/branch/master/graph/badge.svg)](https://codecov.io/gh/castorini/anserini)
[![Generic badge](https://img.shields.io/badge/Lucene-v10.5.0-brightgreen.svg)](https://archive.apache.org/dist/lucene/java/10.5.0/)
[![Maven Central](https://img.shields.io/maven-central/v/io.anserini/anserini?color=brightgreen)](https://central.sonatype.com/namespace/io.anserini)
[![LICENSE](https://img.shields.io/badge/license-Apache-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![doi](http://img.shields.io/badge/doi-10.1145%2F3239571-blue.svg?style=flat)](https://doi.org/10.1145/3239571)

Anserini is a toolkit for reproducible information retrieval research.
By building on Lucene, we aim to bridge the gap between academic information retrieval research and the practice of building real-world search applications.
Among other goals, our effort aims to be [the opposite of this](http://phdcomics.com/comics/archive.php?comicid=1689).[*](docs/reproducibility.md)
Anserini grew out of [a reproducibility study of various open-source retrieval engines in 2016](https://link.springer.com/chapter/10.1007/978-3-319-30671-1_30) (Lin et al., ECIR 2016).
See [Yang et al. (SIGIR 2017)](https://dl.acm.org/doi/10.1145/3077136.3080721) and [Yang et al. (JDIQ 2018)](https://dl.acm.org/doi/10.1145/3239571) for overviews.

**For users steering coding agents:**
If you want a complete dev environment, give your agent the following instructions:

> read the skill at https://github.com/castorini/anserini/tree/master/.agents/skills/install-anserini-dev-env to create a dev environment for me; no need to install the skill

Alternatively, if you want a quick fatjar-based setup, give your agent the following instructions:

> read the skill at https://github.com/castorini/anserini/tree/master/.agents/skills/install-anserini-fatjar to download the fatjar for me; no need to install the skill

<details>
<summary>Rationale...</summary>

> ❓ **Why the above instructions?**
> If you just tell the agent to install Anserini, it'll likely figure everything out by itself, but there are some choice points it might get confused by, like whether to do a full clone for a dev environment, or just fetch the fatjar.
> The skills make this clear and provide instructions that make the installation process more token-efficient.
> If you don't _explicitly_ point your agent at the skills, it may be overeager and wander around...
>
> Also, tell the agent not to install the skill locally, since you only need it once.
> Otherwise, it may do unnecessary work.

</details>

> **For coding agents:** When setting up Anserini, ask whether the user wants a dev environment or a quick fatjar-based setup. Use the [`install-anserini-dev-env`](.agents/skills/install-anserini-dev-env/SKILL.md) skill for the dev environment and the [`install-anserini-fatjar`](.agents/skills/install-anserini-fatjar/SKILL.md) skill for the fatjar setup.
> For command-line usage after setup, use the [`anserini-cli`](.agents/skills/anserini-cli/SKILL.md) skill for prebuilt-index registry, topics registry, search, and REST workflows.
> For reproducibility experiments, use the [`anserini-reproduction`](.agents/skills/anserini-reproduction/SKILL.md).

❗ Anserini previously included [a submodule checkout](https://github.com/castorini/eval/) at `tools/`. This was removed at [`anserini#3382`](https://github.com/castorini/anserini/pull/3382) to eliminate an external dependency. This has a few implications:

+ At commit [`43add83`](https://github.com/castorini/eval/commit/43add835e20bd66b48f9a640be9bad95a4762d82) (2026/08/09), (in what used to be `tools/`) `topics-and-qrels/` was refactored into separate `topics/` and `qrels/` directories.
At the same time, the repo was renamed from `anserini-tools` to `eval`.
The associated PR is [`eval#118`](https://github.com/castorini/eval/pull/118).
This breaks consumers that depend on fetching a stable `topics-and-qrels/` path (on `master`).
Note that the most obvious solution to add symlinks won't work, as `raw.githubusercontent.com` URLs do not automatically redirect.
+ Anserini commit [`9bfc04b`](https://github.com/castorini/anserini/commit/9bfc04b2d5f22e3acf56edf43d06c1efa5fe2783) (2026/08/11) was the first commit that pinned a specific commit (hence ensuring stability).
The associated PR is [`anserini#3369`](https://github.com/castorini/anserini/pull/3369).
This means that any state of the repo before that commit is likely broken.

## 🎬 Installation (for Users)

> This section is intended for users. If you are a coding agent, stop reading and skip the rest of this section.

💥 **Try It!**
Anserini is packaged in a self-contained fatjar, which provides the simplest way to get started: just `curl` the fatjar and you're good to go!
See [this page](docs/installation-fatjar.md) for detailed instructions.

Alternatively, if you want to clone this repo and set up a full dev environment for Anserini, see [this page](docs/installation-dev.md) for instructions.
Most Anserini features are exposed in the [Pyserini](http://pyserini.io/) Python interface, so if you're more comfortable with Python, start there.

The onboarding path for Anserini starts [here](docs/start-here.md)!

## ⚗️ Reproductions from Prebuilt Indexes

> This section is intended for both users and coding agents.

Go to [this reference](docs/ref-reproduce-from-prebuilt-indexes.md) for details on reproducing experimental results on prebuilt indexes.

## ⚗️ Reproductions from Document Collections

> This section is intended for both users and coding agents.

Go to [this reference](docs/ref-reproduce-from-document-collections.md) for details on reproducing experimental results from the raw document collections.

## 📃 Additional Documentation (for Users)

> This section is intended for users. If you are a coding agent, stop reading and skip the rest of this section.

Follow [this link](docs/additional-docs.md) for additional documentation targeted at users.

## ✨ References

+ Jimmy Lin, Matt Crane, Andrew Trotman, Jamie Callan, Ishan Chattopadhyaya, John Foley, Grant Ingersoll, Craig Macdonald, Sebastiano Vigna. [Toward Reproducible Baselines: The Open-Source IR Reproducibility Challenge.](https://link.springer.com/chapter/10.1007/978-3-319-30671-1_30) _ECIR 2016_.
+ Peilin Yang, Hui Fang, and Jimmy Lin. [Anserini: Enabling the Use of Lucene for Information Retrieval Research.](https://dl.acm.org/doi/10.1145/3077136.3080721) _SIGIR 2017_.
+ Peilin Yang, Hui Fang, and Jimmy Lin. [Anserini: Reproducible Ranking Baselines Using Lucene.](https://dl.acm.org/doi/10.1145/3239571) _Journal of Data and Information Quality_, 10(4), Article 16, 2018.
